package de.fxnn.geld.application.model;

import de.fxnn.geld.category.model.CategoryModel;
import de.fxnn.geld.common.model.Model;
import de.fxnn.geld.io.ikonli.CategoryIcon;
import de.fxnn.geld.transaction.model.TransactionModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import lombok.Data;

/**
 * A model for the whole workspace the application provides. That is, one instance serves as root
 * (in the sense of a tree structure) to all state of one application instance.
 */
@Data
public class WorkspaceModel implements Model {

  /** All category models sorted by their icon. A transient property. */
  private final ObservableMap<CategoryIcon, CategoryModel> categoryMap =
      FXCollections.observableHashMap();

  /** All known transactions. A persistent property. */
  private final ObservableList<TransactionModel> transactionList =
      FXCollections.observableArrayList();

  /** The search expression entered by the user. A transient property. */
  private Property<String> filterExpression = new SimpleStringProperty();

  /** The transaction list, filtered through the {@link #filterExpression}. A transient property. */
  private final FilteredList<TransactionModel> filteredTransactionList =
      new FilteredList<>(transactionList, t -> true);

  @Override
  public void updateTransientProperties() {
    categoryMap.values().forEach(CategoryModel::updateTransientProperties);
    transactionList.forEach(this::updateTransientProperties);
  }

  private void updateTransientProperties(TransactionModel transactionModel) {
    transactionModel.updateTransientProperties();
    transactionModel.setCategoryModel(selectFirstMatchingCategory(transactionModel));
  }

  private CategoryModel selectFirstMatchingCategory(TransactionModel transactionModel) {
    for (CategoryModel categoryModel : categoryMap.values()) {
      if (categoryModel.getFilterPredicate().test(transactionModel)) {
        return categoryModel;
      }
    }

    return null;
  }

  public void assignCategoryIcon(CategoryIcon icon, String filterExpression) {
    var categoryModel = getCategoryMap().get(icon);
    if (getCategoryMap().containsKey(icon)) {
      categoryModel.appendFilterExpression(filterExpression);
    } else {
      categoryModel = new CategoryModel();
      categoryModel.setCategoryIcon(icon);
      categoryModel.setFilterExpression(filterExpression);
      getCategoryMap().put(icon, categoryModel);
    }
    updateTransientProperties();
  }
}
