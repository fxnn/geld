package de.fxnn.geld.jfx.transaction;

import com.gluonhq.charm.glisten.control.CharmListView;
import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.time.LocalDate;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TransactionListView extends CharmListView<TransactionModel, LocalDate> {

  public TransactionListView(WorkspaceModel model) {
    super(model.getFilteredTransactionList());
    setCellFactory(TransactionListCell::new);
    setHeadersFunction(TransactionModel::getDate);
    VBox.setVgrow(this, Priority.ALWAYS);
  }
}
