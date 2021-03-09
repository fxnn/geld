package de.fxnn.geld.jfx;

import static de.fxnn.geld.system.I18n.i18n;

import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.jfx.model.ApplicationModel;
import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.sample.TransactionListCell;
import java.io.IOException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class TransactionListView extends View {

  private final ApplicationModel applicationModel;

  public TransactionListView(ApplicationModel applicationModel) {
    this.applicationModel = applicationModel;

    FloatingActionButton fab =
        new FloatingActionButton(MaterialDesignIcon.FOLDER_OPEN.text, this::onButtonOpenClick);
    fab.showOn(this);

    CharmListView<TransactionModel, LocalDate> listView =
        new CharmListView<>(applicationModel.getTransactionList());
    listView.setCellFactory(TransactionListCell::new);
    setCenter(listView);
  }

  private void onButtonOpenClick(ActionEvent event) {
    var fileChooser = new FileChooser();
    var file = fileChooser.showOpenDialog(getScene().getWindow());
    if (file == null) {
      return;
    }

    try {
      var count = applicationModel.loadTransactionList(file);
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
