package de.fxnn.geld.transaction.view;

import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

public class TransactionBarChart extends BarChart<String, Number> {

  /** Number of Months between two adjacent ticks on the categorial axis. */
  private static final int CATEGORY_TICK_INTERVAL = 1;

  private final ObservableList<String> categoryList = FXCollections.observableArrayList();
  private LocalDate earliestDateCategory;
  private LocalDate latestDateCategory;
  private final ObservableList<Data<String, Number>> dataList = FXCollections.observableArrayList();
  /** Keys are categories; a value is the categories' index in {@link #dataList}. */
  private final Map<String, Integer> categoryDataIndexMap = new HashMap<>();

  public TransactionBarChart(WorkspaceModel model) {
    super(new CategoryAxis(), new NumberAxis());

    model.getFilteredTransactionList().forEach(this::addTransaction);
    model.getFilteredTransactionList().addListener(this::onTransactionListChanged);

    ((CategoryAxis) getXAxis()).setCategories(categoryList);
    getData().add(new Series<>(dataList));

    // styling
    setLegendVisible(false);
  }

  private void onTransactionListChanged(Change<? extends TransactionModel> change) {
    while (change.next()) {
      // HINT: #addTransaction establishes the categories, so we need to run that first.
      change.getAddedSubList().forEach(this::addTransaction);
      change.getRemoved().forEach(this::removeTransaction);
    }
  }

  /**
   * Remove a given transaction from the chart.
   *
   * <p>The basic assumption is that, in contrast to {@link #addTransaction(TransactionModel)}, this
   * operation does <em>not</em> modify the category bounds {@link #earliestDateCategory} and {@link
   * #latestDateCategory}. Reason: at the moment, removing transactions only happens during
   * filtering, and as most filters will bring up transactions from a larger date range, we can keep
   * it simple here.
   */
  private void removeTransaction(TransactionModel transactionModel) {
    var transactionMonth = transactionModel.getDate().withDayOfMonth(1);
    var category = formatCategory(transactionMonth);
    var index = categoryDataIndexMap.get(category);
    modifyDataPoint(index, value -> value.subtract(transactionModel.getAmount()));
  }

  private void addTransaction(TransactionModel transactionModel) {
    var transactionMonth = transactionModel.getDate().withDayOfMonth(1);
    updateCategoryBounds(transactionMonth);
    categoryDataIndexMap.compute(
        formatCategory(transactionMonth),
        (category, index) -> {
          if (index == null) {
            // HINT: we always add to the end; the general assumption is that transactions
            //   appear in order
            dataList.add(new Data<>(category, transactionModel.getAmount()));
            return dataList.size() - 1;
          }
          modifyDataPoint(index, value -> value.add(transactionModel.getAmount()));
          return index;
        });
  }

  private void modifyDataPoint(Integer index, Function<BigDecimal, BigDecimal> modification) {
    var data = dataList.get(index);
    var oldValue =
        data.getYValue() instanceof BigDecimal
            ? (BigDecimal) data.getYValue()
            : BigDecimal.valueOf(data.getYValue().doubleValue());
    var newValue = modification.apply(oldValue);

    data.setYValue(newValue);
  }

  private void updateCategoryBounds(LocalDate transactionMonth) {
    var categoryBoundsChanged = false;
    if (this.earliestDateCategory == null || transactionMonth.isBefore(this.earliestDateCategory)) {
      categoryBoundsChanged = true;
      this.earliestDateCategory = transactionMonth;
    }
    if (latestDateCategory == null || transactionMonth.isAfter(latestDateCategory)) {
      categoryBoundsChanged = true;
      latestDateCategory = transactionMonth;
    }
    if (categoryBoundsChanged) {
      categoryList.clear();
      var currentDateCategory = earliestDateCategory;
      while (currentDateCategory.isBefore(latestDateCategory)
          || currentDateCategory.isEqual(latestDateCategory)) {
        categoryList.add(formatCategory(currentDateCategory));
        currentDateCategory = currentDateCategory.plusMonths(CATEGORY_TICK_INTERVAL);
      }
    }
  }

  private String formatCategory(LocalDate currentDateCategory) {
    return currentDateCategory.format(DateTimeFormatter.ofPattern("yyyy-MM"));
  }
}
