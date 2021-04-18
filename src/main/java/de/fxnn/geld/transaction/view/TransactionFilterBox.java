package de.fxnn.geld.transaction.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.category.model.CategoryModel;
import de.fxnn.geld.category.view.CreateCategoryView;
import de.fxnn.geld.transaction.model.FilterParser;
import de.fxnn.geld.transaction.model.TransactionModel;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.unicons.UniconsLine;

public class TransactionFilterBox extends HBox {

  private final WorkspaceModel model;
  private final FilterParser filterParser = new FilterParser();

  public TransactionFilterBox(WorkspaceModel model) {
    this.model = model;
    getChildren().add(createFilterField());
    getChildren().add(createFilterButton());
  }

  private TextField createFilterField() {
    var filterField = new TextField();
    filterField.setPromptText(i18n().message("transaction.filter.prompt"));
    filterField.textProperty().bindBidirectional(model.getFilterExpression());
    filterField.textProperty().addListener(this::onFilterTextChanged);
    HBox.setHgrow(filterField, Priority.ALWAYS);
    return filterField;
  }

  private void onFilterTextChanged(
      ObservableValue<? extends String> observable, String oldValue, String newValue) {
    model.getFilteredTransactionList().setPredicate(filterParser.parse(newValue));
  }

  private Node createFilterButton() {
    var categoryAddMenuItem = new MenuItem(i18n().message("transaction.filter.category.add"));
    categoryAddMenuItem.setGraphic(new FontIcon(UniconsLine.PLUS));
    categoryAddMenuItem.setOnAction(
        e -> MobileApplication.getInstance().switchView(CreateCategoryView.VIEW_NAME));

    var noCategoryFilterMenuItem =
        new MenuItem(i18n().message("transaction.filter.missing.category"));
    noCategoryFilterMenuItem.setGraphic(new FontIcon(TransactionModel.NO_CATEGORY_IKON));
    noCategoryFilterMenuItem.setOnAction(
        e -> model.getFilterExpression().setValue("without:category"));

    var filterButton = new MenuButton();
    filterButton.setGraphic(new FontIcon(UniconsLine.APPS));
    GlistenStyleClasses.applyStyleClass(filterButton, GlistenStyleClasses.BUTTON_FLAT);

    filterButton.getItems().add(categoryAddMenuItem);
    filterButton.getItems().add(noCategoryFilterMenuItem);
    model.getCategoryList().forEach(category -> addCategoryMenuItem(category, filterButton));

    model
        .getCategoryList()
        .addListener(
            (Change<? extends CategoryModel> change) ->
                onCategoryListChanged(change, filterButton));

    return filterButton;
  }

  private void onCategoryListChanged(
      Change<? extends CategoryModel> change, MenuButton filterButton) {
    while (change.next()) {
      if (change.wasAdded()) {
        change.getAddedSubList().forEach(category -> addCategoryMenuItem(category, filterButton));
      }
    }
  }

  private void addCategoryMenuItem(CategoryModel category, MenuButton filterButton) {
    var menuItem = new MenuItem(category.getFilterExpression(), new FontIcon(category.getIkon()));
    menuItem.setOnAction(e -> model.getFilterExpression().setValue(category.getFilterExpression()));
    filterButton.getItems().add(menuItem);
  }
}
