package de.fxnn.geld.jfx;

import static de.fxnn.geld.system.I18n.i18n;

import de.fxnn.geld.io.workspace.WorkspaceModelIo;
import de.fxnn.geld.jfx.model.WorkspaceModel;
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
