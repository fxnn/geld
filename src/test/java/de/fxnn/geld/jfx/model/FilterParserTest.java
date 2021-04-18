package de.fxnn.geld.jfx.model;

import static org.junit.jupiter.api.Assertions.*;

import de.fxnn.geld.transaction.model.FilterParser;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

class FilterParserTest {

  @Test
  void nullString__matchesAll() {
    var predicate = new FilterParser().parse("");

    thenMatchesTransactionWithText(null, predicate);
    thenMatchesTransactionWithText("", predicate);
    thenMatchesTransactionWithText("test", predicate);
  }

  @Test
  void emptyString__matchesAll() {
    var predicate = new FilterParser().parse("");

    thenMatchesTransactionWithText(null, predicate);
    thenMatchesTransactionWithText("", predicate);
    thenMatchesTransactionWithText("test", predicate);
  }

  @Test
  void oneWord__matchesOnlyWhenWordIsPresent() {
    var predicate = new FilterParser().parse("matching");

    thenDoesNotMatchTransactionWithText(null, predicate);
    thenDoesNotMatchTransactionWithText("", predicate);
    thenDoesNotMatchTransactionWithText("fail", predicate);
    thenDoesNotMatchTransactionWithText("nonmatching", predicate);
    thenMatchesTransactionWithText("matching", predicate);
    thenMatchesTransactionWithText("also matching", predicate);
  }

  @Test
  void multipleWord__matchesWhenAnyWordsArePresent() {
    var predicate = new FilterParser().parse("firstMatcher secondMatcher");

    thenDoesNotMatchTransactionWithText(null, predicate);
    thenDoesNotMatchTransactionWithText("", predicate);
    thenDoesNotMatchTransactionWithText("fail", predicate);
    thenDoesNotMatchTransactionWithText("notTheFirstMatcher", predicate);
    thenMatchesTransactionWithText("firstMatcher", predicate);
    thenMatchesTransactionWithText("secondMatcher", predicate);
    thenMatchesTransactionWithText("firstMatcher secondMatcher", predicate);
  }

  @Test
  void quotedString__performsSubstringMatch() {
    var predicate = new FilterParser().parse("\"quoted string\"");

    thenDoesNotMatchTransactionWithText(null, predicate);
    thenDoesNotMatchTransactionWithText("", predicate);
    thenDoesNotMatchTransactionWithText("fail", predicate);
    thenDoesNotMatchTransactionWithText("string quoted", predicate);
    thenDoesNotMatchTransactionWithText("\"quoted\" \"string\"", predicate);
    thenMatchesTransactionWithText("this is a \"quoted string\" thing", predicate);
    thenMatchesTransactionWithText("this is a quoted string thing", predicate);
    thenMatchesTransactionWithText("subquoted stringthing", predicate);
  }

  @Test
  void twoQuotedStrings__performsSubstringMatches() {
    var predicate = new FilterParser().parse("\"criterion 1\" \"criterion 2\"");

    thenMatchesTransactionWithText("criterion 1", predicate);
    thenMatchesTransactionWithText("criterion 2", predicate);
    thenMatchesTransactionWithText("criterion 1 criterion 2", predicate);
    thenMatchesTransactionWithText("criterion 2 criterion 1", predicate);
    thenDoesNotMatchTransactionWithText("criterion X 1 2", predicate);
  }

  private void thenMatchesTransactionWithText(String text, Predicate<TransactionModel> predicate) {
    assertTrue(predicate.test(createTransactionalModelWithText(text)));
  }

  private void thenDoesNotMatchTransactionWithText(
      String text, Predicate<TransactionModel> predicate) {
    assertFalse(predicate.test(createTransactionalModelWithText(text)));
  }

  @Nonnull
  private TransactionModel createTransactionalModelWithText(String text) {
    var transactionModel = new TransactionModel();
    transactionModel.setReferenceText(text);
    transactionModel.updateTransientProperties();
    return transactionModel;
  }
}
