package de.fxnn.geld.jfx.transaction;

import static de.fxnn.geld.system.I18n.i18n;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.jfx.model.FilterParser;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class TransactionFilterBox extends HBox {

  private final WorkspaceModel model;
  private final FilterParser filterParser = new FilterParser();

  public TransactionFilterBox(WorkspaceModel model) {
    this.model = model;
    getChildren().add(createFilterField());
    getChildren().add(createLabelButton());
  }

  private void onFilterTextChanged(
      ObservableValue<? extends String> observable, String oldValue, String newValue) {
    model.getFilteredTransactionList().setPredicate(filterParser.parse(newValue));
  }

  private Node createLabelButton() {
    var labelAddMenuItem = new MenuItem(i18n().message("transaction.filter.label.add"));
    labelAddMenuItem.setOnAction(
        e -> MobileApplication.getInstance().switchView(CreateLabelView.VIEW_NAME));

    var labelButton = new MenuButton();
    labelButton.setGraphic(MaterialDesignIcon.LABEL.graphic());
    labelButton.getItems().add(labelAddMenuItem);
    GlistenStyleClasses.applyStyleClass(labelButton, GlistenStyleClasses.BUTTON_FLAT);

    return labelButton;
  }

  private TextField createFilterField() {
    var filterField = new TextField();
    filterField.setPromptText(i18n().message("transaction.filter.prompt"));
    filterField.textProperty().addListener(this::onFilterTextChanged);
    HBox.setHgrow(filterField, Priority.ALWAYS);
    return filterField;
  }
}
