package de.fxnn.geld.jfx.transaction;

import com.gluonhq.charm.glisten.mvc.View;
import de.fxnn.geld.io.ikonli.LabelIcon;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.javafx.FontIcon;

@Slf4j
public class CreateLabelView extends View {

  public static final String VIEW_NAME = "create-label";

  private final GridPane gridPane;

  public CreateLabelView() {
    gridPane = new GridPane();
    gridPane.setPadding(new Insets(5d));
    gridPane.setVgap(5d);
    gridPane.setHgap(5d);

    int iconIndex = 0;
    for (LabelIcon icon : LabelIcon.values()) {
      addEmoji(icon, iconIndex++, 10);
    }

    var scrollPane = new ScrollPane(gridPane);
    scrollPane.setBorder(Border.EMPTY);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    setCenter(new VBox(scrollPane));
  }

  private void addEmoji(LabelIcon icon, int iconIndex, int iconsPerRow) {
    var fontIcon = new FontIcon(icon.getIkon());
    fontIcon.setIconSize(32);

    var button = new Button();
    button.setGraphic(fontIcon);
    button.getStyleClass().add("select-emoji");

    gridPane.add(button, iconIndex % iconsPerRow, iconIndex / iconsPerRow);
  }
}
