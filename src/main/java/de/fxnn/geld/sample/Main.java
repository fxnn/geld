package de.fxnn.geld.sample;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import de.fxnn.geld.jfx.TransactionListView;
import de.fxnn.geld.jfx.model.ApplicationModel;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;

public class Main extends MobileApplication {

  private final ApplicationModel model = new ApplicationModel();

  @Override
  public void init() {
    addViewFactory(HOME_VIEW, () -> new TransactionListView(model));
  }

  @Override
  public void postInit(Scene scene) {
    Swatch.LIGHT_GREEN.assignTo(scene);
    scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());

    if (Platform.isDesktop()) {
      Dimension2D dimension2D =
          DisplayService.create()
              .map(DisplayService::getDefaultDimensions)
              .orElse(new Dimension2D(640, 480));
      scene.getWindow().setWidth(dimension2D.getWidth());
      scene.getWindow().setHeight(dimension2D.getHeight());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
