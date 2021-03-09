package de.fxnn.geld.jfx.model;

import de.fxnn.geld.io.mt940.Mt940Loader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class ApplicationModel {

  private final ObservableList<TransactionModel> transactionList =
      FXCollections.observableArrayList();

  public int loadTransactionList(File file) throws IOException {
    var models =
        new Mt940Loader()
            .loadTransactionModels(file)
            .map(TransactionModel::fromMt940Message)
            .flatMap(ArrayList::stream)
            .collect(Collectors.toList());
    transactionList.clear();
    transactionList.addAll(models);
    return models.size();
  }
}
