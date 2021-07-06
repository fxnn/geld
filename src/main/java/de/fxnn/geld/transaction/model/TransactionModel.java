package de.fxnn.geld.transaction.model;

import de.fxnn.geld.category.model.CategoryModel;
import de.fxnn.geld.common.model.Model;
import de.fxnn.geld.io.mt940.Mt940BalanceField;
import de.fxnn.geld.io.mt940.Mt940Message;
import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.unicons.UniconsLine;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionModel implements Model, Comparable<TransactionModel> {

  public static final Ikon NO_CATEGORY_IKON = UniconsLine.QUESTION_CIRCLE;
  /**
   * This comparator is used to sort all known transactions in the {@code WorkspaceModel}. It must
   * thus provide a total ordering of transactions, even for transactions from different files.
   */
  public static final Comparator<TransactionModel> COMPARATOR =
      Comparator.comparing(TransactionModel::getDate)
          .thenComparing(TransactionModel::getSerialTransactionImportNumber);

  @EqualsAndHashCode.Include private BigDecimal amount;
  @EqualsAndHashCode.Include private String transactionDescription;
  @EqualsAndHashCode.Include private String referenceText;
  @EqualsAndHashCode.Include private String beneficiary;
  @EqualsAndHashCode.Include private LocalDate date;

  /**
   * HINT: Not included in {@link EqualsAndHashCode}, as this is purely informational and thus low
   * priority. As there's always the risk that information on the same transaction differs in two
   * different files, it's safer to only use important fields.
   */
  private BigDecimal balanceBefore;

  /**
   * HINT: Not included in {@link EqualsAndHashCode}, as this is purely informational and thus low
   * priority. As there's always the risk that information on the same transaction differs in two
   * different files, it's safer to only use important fields.
   */
  private BigDecimal balanceAfter;

  /**
   * Serial number of the transaction within the file it was imported from.
   *
   * <p>We don't include this in {@link EqualsAndHashCode}. Reason: when one transaction is included
   * in multiple files, they might have a different total number of transactions and thus different
   * serial numbers. Therefore it's unreliable for equality.
   */
  private long serialTransactionImportNumber;

  /**
   * Transient property containing all words in all other textual properties. Used for full-text
   * search.
   */
  private final Set<String> lowercaseWords = new HashSet<>();

  /** Transient property referring to the category this transaction belongs to. */
  @Nullable private CategoryModel categoryModel;

  @Override
  public void updateTransientProperties() {
    calculateWords();
  }

  public boolean containsLowerCaseWord(String word) {
    return lowercaseWords.contains(word);
  }

  public boolean containsSubString(String value) {
    return streamTextFields().filter(Objects::nonNull).anyMatch(s -> s.contains(value));
  }

  private void calculateWords() {
    lowercaseWords.clear();
    streamTextFields()
        .filter(Objects::nonNull)
        .flatMap(this::splitWords)
        .map(String::toLowerCase)
        .forEach(lowercaseWords::add);
  }

  private Stream<String> streamTextFields() {
    return Stream.of(transactionDescription, referenceText, beneficiary);
  }

  private Stream<String> splitWords(String text) {
    var builder = Stream.<String>builder();
    Stream.of(text.split("\\s+")).forEach(builder::add);
    Stream.of(text.split("\\W+")).forEach(builder::add);
    return builder.build();
  }

  public static ArrayList<TransactionModel> fromMt940Message(Mt940Message message) {
    var result = new ArrayList<TransactionModel>();
    var balanceBefore = message.getHeader(Mt940BalanceField.class).getAmountAsBigDecimal();
    for (Transaction tx : message.getTransactions()) {
      var infoField = tx.getInformationField();
      var txField = tx.getTransactionField();

      var txView = new TransactionModel();
      txView.setSerialTransactionImportNumber(tx.getSerialTransactionImportNumber());
      txView.setBalanceBefore(balanceBefore);
      txView.setAmount(txField.getAmountAsBigDecimal());
      txView.setTransactionDescription(infoField.getTransactionDescription());
      txView.setDate(txField.getDate());
      txView.setReferenceText(
          Optional.ofNullable(infoField.getSepaReferenceText())
              .orElse(infoField.getReferenceText()));
      txView.setBeneficiary(
          Optional.ofNullable(infoField.getSepaUltimatePrincipal())
              .orElse(infoField.getBeneficiaryName()));
      balanceBefore = balanceBefore.add(txField.getAmountAsBigDecimal());
      txView.setBalanceAfter(balanceBefore);
      txView.updateTransientProperties();

      result.add(txView);
    }
    return result;
  }

  public Ikon getCategoryIkon() {
    return getOptionalCategoryModel().map(CategoryModel::getIkon).orElse(NO_CATEGORY_IKON);
  }

  public Optional<CategoryModel> getOptionalCategoryModel() {
    return Optional.ofNullable(getCategoryModel());
  }

  @Override
  public int compareTo(TransactionModel o) {
    return COMPARATOR.compare(this, o);
  }
}
