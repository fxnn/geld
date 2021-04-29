package de.fxnn.geld.transaction.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.control.Alert;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.application.view.WorkspaceSaveAction;
import de.fxnn.geld.common.view.AlertFactory;
import de.fxnn.geld.io.mt940.Mt940Loader;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    chooseFile().ifPresent(this::loadTransactions);
  }

  private Optional<Path> chooseFile() {
    var fileChooser = new FileChooser();
    var file = fileChooser.showOpenDialog(parent.getScene().getWindow());
    return Optional.ofNullable(file).map(File::toPath).map(Path::toAbsolutePath);
  }

  private void loadTransactions(Path path) {
    try {
      var transactions = loadMt940Transactions(path);
      model.getTransactionList().addAll(transactions);
      model.updateTransientProperties();
      Platform.runLater(workspaceSaveAction);
      new Alert(
              AlertType.INFORMATION,
              i18n().formatMessage("transaction.load.success", transactions.size()))
          .showAndWait();
    } catch (IOException ex) {
      log.warn("Failed to load transaction file", ex);
      alertFactory
          .createError(i18n().formatMessage("transaction.load.failed", path), ex)
          .showAndWait();
    }
  }

  private List<TransactionModel> loadMt940Transactions(Path path) throws IOException {
    return new Mt940Loader()
        .loadTransactionModels(path)
        .map(TransactionModel::fromMt940Message)
        .flatMap(ArrayList::stream)
        .collect(Collectors.toList());
  }
}
