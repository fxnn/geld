package de.fxnn.geld.jfx.transaction;

import static de.fxnn.geld.system.I18n.i18n;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.jfx.FloatingViewSwitchButton;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisualizeTransactionView extends View {

  public static final String VIEW_NAME = "visualize-transaction";

  public VisualizeTransactionView(WorkspaceModel model) {
    setCenter(new VBox(new TransactionFilterBox(model), new TransactionBarChart(model)));
    new FloatingViewSwitchButton(MaterialDesignIcon.VIEW_LIST, ShowTransactionView.VIEW_NAME)
        .showOn(this);
  }

  @Override
  protected void updateAppBar(AppBar appBar) {
    appBar.setTitleText(i18n().message("application.header"));
  }
}
