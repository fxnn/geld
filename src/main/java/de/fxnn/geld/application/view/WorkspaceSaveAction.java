package de.fxnn.geld.application.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.application.platform.ApplicationMetadata;
import de.fxnn.geld.common.view.AlertFactory;
import de.fxnn.geld.io.workspace.WorkspaceModelIo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WorkspaceSaveAction implements Runnable {

  public static final String WORKSPACE_JSON_FILENAME = "workspace.json";

  private final WorkspaceModel workspaceModel;
  private final ApplicationMetadata applicationMetadata;
  private final WorkspaceModelIo workspaceModelIo = new WorkspaceModelIo();
  private final AlertFactory alertFactory = new AlertFactory();

  @Override
  public void run() {
    try {
      var dir = applicationMetadata.getAppConfigPath();
      Files.createDirectories(dir);

      var outputFile = dir.resolve(WORKSPACE_JSON_FILENAME);
      workspaceModelIo.store(
          workspaceModel,
          outputFile,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);

      log.info("Stored Workspace State at {}", outputFile);
    } catch (IOException ex) {
      log.warn("Failed to store Workspace state", ex);
      alertFactory.createWarning(i18n().message("workspace.save.failed"), ex).showAndWait();
    }
  }
}
