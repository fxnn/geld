package de.fxnn.geld.jfx.transaction;

import static de.fxnn.geld.system.I18n.i18n;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.jfx.ApplicationMetadata;
import de.fxnn.geld.jfx.WorkspaceSaveAction;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import javafx.scene.layout.VBox;

public class ShowTransactionView extends View {

  public static final String VIEW_NAME = MobileApplication.HOME_VIEW;

  public ShowTransactionView(WorkspaceModel model, ApplicationMetadata applicationMetadata) {
    var workspaceSaveAction = new WorkspaceSaveAction(model, applicationMetadata);
    var transactionLoadAction = new TransactionLoadAction(this, model, workspaceSaveAction);

    new FloatingActionButton(MaterialDesignIcon.FOLDER_OPEN.text, transactionLoadAction)
        .showOn(this);

    setCenter(new VBox(new TransactionFilterBox(model), new TransactionListView(model)));
  }

  @Override
  protected void updateAppBar(AppBar appBar) {
    appBar.setTitleText(i18n().message("application.header"));
  }
}
