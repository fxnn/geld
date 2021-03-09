package de.fxnn.geld.jfx.model;

import de.fxnn.geld.io.mt940.Mt940BalanceField;
import de.fxnn.geld.io.mt940.Mt940Message;
import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import lombok.Data;

@Data
public class TransactionModel {

  private BigDecimal balanceBefore;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String transactionDescription;
  private String referenceText;
  private String beneficiary;

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
      txView.setReferenceText(
          Optional.ofNullable(infoField.getSepaReferenceText())
              .orElse(infoField.getReferenceText()));
      txView.setBeneficiary(
          Optional.ofNullable(infoField.getSepaUltimatePrincipal())
              .orElse(infoField.getBeneficiaryName()));
      balanceBefore = balanceBefore.add(txField.getAmountAsBigDecimal());
      txView.setBalanceAfter(balanceBefore);

      result.add(txView);
    }
    return result;
  }
}
