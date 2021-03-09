package de.fxnn.geld.jfx;

import static de.fxnn.geld.system.I18n.i18n;

import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import de.fxnn.geld.jfx.model.FilterParser;
import de.fxnn.geld.jfx.model.TransactionModel;
import java.io.IOException;
import java.time.LocalDate;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class TransactionListView extends View {

  private final WorkspaceModel model;
  private final FilterParser filterParser = new FilterParser();

  public TransactionListView(WorkspaceModel model) {
    this.model = model;

    var fab =
        new FloatingActionButton(MaterialDesignIcon.FOLDER_OPEN.text, this::onButtonOpenClick);
    fab.showOn(this);

    var listView =
        new CharmListView<TransactionModel, LocalDate>(
            model.getFilteredTransactionList());
    listView.setCellFactory(TransactionListCell::new);
    listView.setHeadersFunction(TransactionModel::getDate);

    var filterField = new TextField();
    filterField.setPromptText(i18n().message("transaction.filter.prompt"));
    filterField.textProperty().addListener(this::onFilterTextChanged);

    var vbox = new VBox();
    vbox.getChildren().add(filterField);
    vbox.getChildren().add(listView);
    VBox.setVgrow(listView, Priority.ALWAYS);

    setCenter(vbox);
  }

  private void onFilterTextChanged(
      ObservableValue<? extends String> observable, String oldValue, String newValue) {
    model.getFilteredTransactionList().setPredicate(filterParser.parse(newValue));
  }

  private void onButtonOpenClick(ActionEvent event) {
    var fileChooser = new FileChooser();
    var file = fileChooser.showOpenDialog(getScene().getWindow());
    if (file == null) {
      return;
    }

    try {
      var count = model.loadTransactionList(file);
      new Alert(AlertType.INFORMATION, i18n().formatMessage("transaction.load.success", count))
          .showAndWait();
    } catch (IOException ex) {
      var alert =
          new Alert(
              AlertType.ERROR,
              i18n().formatMessage("transaction.load.failed", file.getAbsolutePath()));
      alert.showAndWait();
    }
  }

  @Override
  protected void updateAppBar(AppBar appBar) {
    appBar.setTitleText(i18n().message("application.header"));
  }
}
