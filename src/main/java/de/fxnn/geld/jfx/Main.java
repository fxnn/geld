package de.fxnn.geld.jfx;

import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import de.fxnn.geld.io.workspace.WorkspaceModelIo;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import de.fxnn.geld.jfx.transaction.CreateCategoryView;
import de.fxnn.geld.jfx.transaction.ShowTransactionView;
import java.io.IOException;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.scene.Scene;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
public class Main extends MobileApplication {

  private final WorkspaceModel model;
  private final ApplicationMetadata applicationMetadata = ApplicationMetadata.create();

  public Main() {
    this.model = loadWorkspaceModel();
  }

  /**
   * Called after application object is constructed, but before JavaFX initialization (no Stage,
   * Scene, whatsoever).
   */
  @Override
  public void init() {
    addViewFactory(
        ShowTransactionView.VIEW_NAME, () -> new ShowTransactionView(model, applicationMetadata));
    addViewFactory(CreateCategoryView.VIEW_NAME, () -> new CreateCategoryView(model));
  }

  /**
   * Called after JavaFX initialization is completed, right on the JavaFX Application Thread.
   *
   * <p>This is the {@code MobileApplication} equivalent to {@link
   * Application#start(javafx.stage.Stage)}.
   */
  @Override
  public void postInit(Scene scene) {
    Swatch.LIGHT_GREEN.assignTo(scene);

    loadStylesheetResource(scene, "styles.css");
    if (SystemUtils.IS_OS_MAC_OSX) {
      loadStylesheetResource(scene, "macos.css");
    }

    if (Platform.isDesktop()) {
      scene.getWindow().setWidth(640);
      scene.getWindow().setHeight(480);
    }
  }

  private void loadStylesheetResource(Scene scene, String resourceName) {
    scene.getStylesheets().add(Main.class.getResource(resourceName).toExternalForm());
  }

  private WorkspaceModel loadWorkspaceModel() {
    try {
      var workspaceFile =
          applicationMetadata
              .getAppConfigPath()
              .resolve(WorkspaceSaveAction.WORKSPACE_JSON_FILENAME);
      if (Files.exists(workspaceFile)) {
        log.info("Loading Workspace Model from {}", workspaceFile);
        return new WorkspaceModelIo().load(workspaceFile);
      }
    } catch (IOException ex) {
      log.warn("Failed to load Workspace Model", ex);
    }
    return new WorkspaceModel();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
