package de.fxnn.geld.transaction.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.control.Alert;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.application.view.WorkspaceSaveAction;
import de.fxnn.geld.common.view.AlertFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TransactionLoadAction implements EventHandler<ActionEvent> {

  private final Node parent;
  private final WorkspaceModel model;
  private final WorkspaceSaveAction workspaceSaveAction;
  private final AlertFactory alertFactory = new AlertFactory();

  @Override
  public void handle(ActionEvent event) {
    var fileChooser = new FileChooser();
    var file = fileChooser.showOpenDialog(parent.getScene().getWindow());
    if (file == null) {
      return;
    }

    try {
      var count = model.loadTransactionList(file);
      Platform.runLater(workspaceSaveAction);
      new Alert(AlertType.INFORMATION, i18n().formatMessage("transaction.load.success", count))
          .showAndWait();
    } catch (IOException ex) {
      log.warn("Failed to load transaction file", ex);
      alertFactory
          .createError(i18n().formatMessage("transaction.load.failed", file.getAbsolutePath()), ex)
          .showAndWait();
    }
  }
}
