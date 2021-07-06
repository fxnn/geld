package de.fxnn.geld.transaction.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.application.platform.ApplicationMetadata;
import de.fxnn.geld.application.view.WorkspaceSaveAction;
import de.fxnn.geld.common.view.FloatingViewSwitchButton;
import de.fxnn.geld.transaction.core.TransactionImporter;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.unicons.UniconsLine;

public class ShowTransactionView extends View {

  public static final String VIEW_NAME = MobileApplication.HOME_VIEW;

  private final Button transactionLoadButton;

  public ShowTransactionView(WorkspaceModel model, ApplicationMetadata applicationMetadata) {
    var workspaceSaveAction = new WorkspaceSaveAction(model, applicationMetadata);
    var transactionImporter = new TransactionImporter(model);
    var transactionLoadAction =
        new TransactionLoadAction(this, transactionImporter, workspaceSaveAction);
    model
        .getCategoryMap()
        .addListener((InvalidationListener) ignored -> Platform.runLater(workspaceSaveAction));

    transactionLoadButton = new Button();
    transactionLoadButton.setGraphic(new FontIcon(UniconsLine.FOLDER_OPEN));
    transactionLoadButton.setOnAction(transactionLoadAction);

    new FloatingViewSwitchButton(
            MaterialDesignIcon.INSERT_CHART, VisualizeTransactionView.VIEW_NAME)
        .showOn(this);

    setCenter(new VBox(new TransactionFilterBox(model), new TransactionListView(model)));
  }

  @Override
  protected void updateAppBar(AppBar appBar) {
    appBar.setTitleText(i18n().message("application.header"));
    appBar.getActionItems().add(transactionLoadButton);
  }
}
