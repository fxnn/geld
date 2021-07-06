package de.fxnn.geld.io.workspace;

import com.dslplatform.json.CompiledJson;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Value;

@Value
@CompiledJson
public class ExternalTransaction {

  Long serialTransactionImportNumber;
  BigDecimal balanceBefore;
  BigDecimal amount;
  BigDecimal balanceAfter;
  String transactionDescription;
  String referenceText;
  String beneficiary;
  LocalDate date;
}
