package de.fxnn.geld.transaction.core;

import static org.junit.jupiter.api.Assertions.*;

import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.transaction.model.TransactionModelFixtures;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionImporterTest {

  WorkspaceModel model;

  @BeforeEach
  void setUp() {
    model = new WorkspaceModel();
  }

  @Test
  void importTransactions__firstTransaction__noDuplicates() {
    new TransactionImporter(model)
        .importTransactions(List.of(TransactionModelFixtures.someTransaction().build()));

    assertEquals(1, model.getTransactionList().size());
  }

  @Test
  void importTransactions__exactlySameTransaction__noDuplicates() {
    var existingTransaction = TransactionModelFixtures.someTransaction().build();
    model.getTransactionList().add(existingTransaction);

    new TransactionImporter(model)
        .importTransactions(List.of(TransactionModelFixtures.someTransaction().build()));

    assertEquals(1, model.getTransactionList().size());
  }

  @Test
  void importTransactions__transactionEqualsButDifferent__noDuplicates() {
    var existingTransaction = TransactionModelFixtures.someTransaction().build();
    model.getTransactionList().add(existingTransaction);

    new TransactionImporter(model)
        .importTransactions(
            List.of(
                TransactionModelFixtures.someTransaction()
                    // HINT: make the transaction differ in all attributes that are not relevant
                    //   for equality
                    .balanceBefore(BigDecimal.ZERO)
                    .balanceAfter(BigDecimal.ZERO)
                    .serialTransactionImportNumber(0L)
                    .build()));

    assertEquals(1, model.getTransactionList().size());
  }
}
