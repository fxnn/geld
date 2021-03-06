package de.fxnn.geld.sample;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransactionView {

  private BigDecimal balanceBefore;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String transactionDescription;
  private String referenceText;
  private String beneficiary;
}
