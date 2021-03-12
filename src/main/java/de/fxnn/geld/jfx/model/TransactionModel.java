package de.fxnn.geld.jfx.model;

import de.fxnn.geld.io.mt940.Mt940BalanceField;
import de.fxnn.geld.io.mt940.Mt940Message;
import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;

@Data
public class TransactionModel implements Model {

  private BigDecimal balanceBefore;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String transactionDescription;
  private String referenceText;
  private String beneficiary;
  private LocalDate date;

  /**
   * Transient property containing all words in all other textual properties. Used for full-text
   * search.
   */
  private final Set<String> lowercaseWords = new HashSet<>();

  @Override
  public void updateTransientProperties() {
    calculateWords();
  }

  private void calculateWords() {
    lowercaseWords.clear();
    Stream.of(transactionDescription, referenceText, beneficiary)
        .filter(Objects::nonNull)
        .flatMap(this::splitWords)
        .map(String::toLowerCase)
        .forEach(lowercaseWords::add);
  }

  private Stream<String> splitWords(String text) {
    var builder = Stream.<String>builder();
    Stream.of(text.split("\\s+")).forEach(builder::add);
    Stream.of(text.split("\\W+")).forEach(builder::add);
    return builder.build();
  }

  public boolean containsAnyLowerCase(List<String> criteria) {
    return criteria.stream().anyMatch(lowercaseWords::contains);
  }

  public static ArrayList<TransactionModel> fromMt940Message(Mt940Message message) {
    var result = new ArrayList<TransactionModel>();
    var balanceBefore = message.getHeader(Mt940BalanceField.class).getAmountAsBigDecimal();
    for (Transaction tx : message.getTransactions()) {
      var infoField = tx.getInformationField();
      var txField = tx.getTransactionField();

      var txView = new TransactionModel();
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
}
