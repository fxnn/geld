package de.fxnn.geld.jfx.model;

import de.fxnn.geld.io.mt940.Mt940Loader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Data;

/**
 * A model for the whole workspace the application provides. That is, one instance serves as root
 * (in the sense of a tree structure) to all state of one application instance.
 */
@Data
public class WorkspaceModel implements Model {

  private final ObservableList<CategoryModel> categoryList = FXCollections.observableArrayList();

  private final ObservableList<TransactionModel> transactionList =
      FXCollections.observableArrayList();

  /** The search expression entered by the user. */
  private Property<String> filterExpression = new SimpleStringProperty();

  /** The transaction list, filtered through the {@link #filterExpression}. */
  private final FilteredList<TransactionModel> filteredTransactionList =
      new FilteredList<>(transactionList, t -> true);

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

  @Override
  public void updateTransientProperties() {
    categoryList.forEach(CategoryModel::updateTransientProperties);
    transactionList.forEach(this::updateTransientProperties);
  }

  private void updateTransientProperties(TransactionModel transactionModel) {
    transactionModel.updateTransientProperties();
    transactionModel.setCategoryModel(selectFirstMatchingCategory(transactionModel));
  }

  private CategoryModel selectFirstMatchingCategory(TransactionModel transactionModel) {
    for (CategoryModel categoryModel : categoryList) {
      if (categoryModel.getFilterPredicate().test(transactionModel)) {
        return categoryModel;
      }
    }

    return null;
  }
}
