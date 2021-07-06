package de.fxnn.geld.transaction.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TransactionModelFixtures {

  private TransactionModelFixtures() {
    // don't instantiate me
  }

  public static TransactionModel.TransactionModelBuilder someTransaction() {
    return TransactionModel.builder()
        .serialTransactionImportNumber(99L)
        .date(LocalDate.of(2018, 5, 30))
        .balanceBefore(BigDecimal.valueOf(100L))
        .amount(BigDecimal.valueOf(42L))
        .balanceAfter(BigDecimal.valueOf(142L))
        .beneficiary("ACME Inc.")
        .transactionDescription("BANKTRANSFER")
        .referenceText("Thank you for your order 987654321 at ACME Inc.");
  }
}
