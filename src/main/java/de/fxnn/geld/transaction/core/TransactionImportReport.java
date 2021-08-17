package de.fxnn.geld.transaction.core;

import lombok.Value;

@Value
public class TransactionImportReport {

  int loadedTransactions;
  int newTransactions;
  int updatedTransactions;

  int beforeTotalTransactions;
  int afterTotalTransactions;
}
