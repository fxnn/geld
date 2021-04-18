package de.fxnn.geld.category.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.category.model.CategoryModel;
import de.fxnn.geld.io.ikonli.CategoryIcon;
import de.fxnn.geld.transaction.view.ShowTransactionView;
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
public class CreateCategoryView extends View {

  public static final String VIEW_NAME = "create-category";

  private final GridPane gridPane;
  private final WorkspaceModel model;

  public CreateCategoryView(WorkspaceModel model) {
    this.model = model;

    gridPane = new GridPane();
    gridPane.setPadding(new Insets(5d));
    gridPane.setVgap(5d);
    gridPane.setHgap(5d);

    int iconIndex = 0;
    for (CategoryIcon icon : CategoryIcon.values()) {
      addIcon(icon, iconIndex++, 10);
    }

    var scrollPane = new ScrollPane(gridPane);
    scrollPane.setBorder(Border.EMPTY);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);

    setCenter(new VBox(scrollPane));
  }

  private void addIcon(CategoryIcon icon, int iconIndex, int iconsPerRow) {
    var fontIcon = new FontIcon(icon.getIkon());
    fontIcon.setIconSize(32);

    var button = new Button();
    button.setGraphic(fontIcon);
    button.getStyleClass().add("select-icon");
    button.getStyleClass().add("flat");
    button.setOnAction(e -> createCategory(icon));

    gridPane.add(button, iconIndex % iconsPerRow, iconIndex / iconsPerRow);
  }

  private void createCategory(CategoryIcon icon) {
    CategoryModel categoryModel = new CategoryModel();
    categoryModel.setCategoryIcon(icon);
    categoryModel.setFilterExpression(model.getFilterExpression().getValue());
    model.getCategoryList().add(categoryModel);
    model.updateTransientProperties();

    MobileApplication.getInstance().switchView(ShowTransactionView.VIEW_NAME);
  }

  @Override
  protected void updateAppBar(AppBar appBar) {
    appBar.setTitleText(i18n().message("label.create.view.title"));
  }
}
