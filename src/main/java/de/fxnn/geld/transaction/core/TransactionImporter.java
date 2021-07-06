package de.fxnn.geld.transaction.core;

import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionImporter {

  private final WorkspaceModel model;

  public void importTransactions(Collection<TransactionModel> newTransactions) {
    Map<TransactionModel, Integer> transactionIndexMap = mapByIndex(model.getTransactionList());

    for (TransactionModel transaction : newTransactions) {
      Integer index = transactionIndexMap.get(transaction);
      if (index == null) {
        model.getTransactionList().add(transaction);
      } else {
        // NOTE: equality of transactions is not defined on all their fields, so
        //   replacing after equality check does make sense, as it might update some
        //   of the fields.
        model.getTransactionList().set(index, transaction);
      }
    }

    model.getTransactionList().sort(TransactionModel.COMPARATOR);
    model.updateTransientProperties();
  }

  private Map<TransactionModel, Integer> mapByIndex(List<TransactionModel> transactionList) {
    Map<TransactionModel, Integer> transactionIndexMap = new HashMap<>();
    for (int i = 0; i < transactionList.size(); i++) {
      transactionIndexMap.put(transactionList.get(i), i);
    }
    return transactionIndexMap;
  }
}
