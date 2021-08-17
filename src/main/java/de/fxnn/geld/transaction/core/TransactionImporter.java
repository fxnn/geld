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

  public TransactionImportReport importTransactions(Collection<TransactionModel> newTransactions) {
    int beforeTotalTransactionCount = model.getTransactionList().size();
    int loadedTransactionCount = newTransactions.size();
    Map<TransactionModel, Integer> transactionIndexMap = mapByIndex(model.getTransactionList());

    int newTransactionCount = 0;
    int updatedTransactionCount = 0;
    for (TransactionModel transaction : newTransactions) {
      Integer index = transactionIndexMap.get(transaction);
      if (index == null) {
        model.getTransactionList().add(transaction);
        newTransactionCount++;
      } else {
        // NOTE: equality of transactions is not defined on all their fields, so
        //   replacing after equality check does make sense, as it might update some
        //   of the fields.
        model.getTransactionList().set(index, transaction);
        updatedTransactionCount++;
      }
    }

    model.getTransactionList().sort(TransactionModel.COMPARATOR);
    model.updateTransientProperties();

    int afterTotalTransactionCount = model.getTransactionList().size();
    return new TransactionImportReport(
        loadedTransactionCount,
        newTransactionCount,
        updatedTransactionCount,
        beforeTotalTransactionCount,
        afterTotalTransactionCount);
  }

  private Map<TransactionModel, Integer> mapByIndex(List<TransactionModel> transactionList) {
    Map<TransactionModel, Integer> transactionIndexMap = new HashMap<>();
    for (int i = 0; i < transactionList.size(); i++) {
      transactionIndexMap.put(transactionList.get(i), i);
    }
    return transactionIndexMap;
  }
}
