package de.fxnn.geld.transaction.view;

import com.gluonhq.charm.glisten.control.CharmListView;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.transaction.model.TransactionModel;
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
