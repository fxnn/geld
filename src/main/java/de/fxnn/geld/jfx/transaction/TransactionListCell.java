package de.fxnn.geld.jfx.transaction;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import de.fxnn.geld.jfx.model.TransactionModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TransactionListCell extends CharmListCell<TransactionModel> {

  private static final String LIST_CELL_VBOX_ID = "TransactionListCellVBox";
  private static final String AMOUNT_POSITIVE_CLASS = "positiveAmount";
  private static final String AMOUNT_NEGATIVE_CLASS = "negativeAmount";

  private final Label amount = new Label();
  private final Label beneficiary = new Label();
  private final Label referenceText = new Label();
  private final HBox keyLine = new HBox();
  private final VBox root = new VBox();

  public TransactionListCell(CharmListView<TransactionModel, LocalDate> listView) {
    prefWidthProperty().bind(listView.widthProperty());
    setMaxWidth(USE_PREF_SIZE);

    amount.setPrefWidth(70.0d);
    amount.setMinWidth(Region.USE_PREF_SIZE);
    amount.setAlignment(Pos.BASELINE_RIGHT);
    amount.getStyleClass().add(AMOUNT_POSITIVE_CLASS);
    keyLine.getChildren().addAll(amount, beneficiary);
    keyLine.setSpacing(10.0d);
    HBox.setHgrow(amount, Priority.ALWAYS);
    root.getChildren().addAll(keyLine, referenceText);
    root.setId(LIST_CELL_VBOX_ID);
  }

  @Override
  public void updateItem(TransactionModel item, boolean empty) {
    super.updateItem(item, empty);
    if (empty) {
      setGraphic(null);
    } else {
      amount.setText(item.getAmount().toString());
      if (item.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
        amount.getStyleClass().set(0, AMOUNT_POSITIVE_CLASS);
      } else {
        amount.getStyleClass().set(0, AMOUNT_NEGATIVE_CLASS);
      }
      beneficiary.setText(item.getBeneficiary());
      referenceText.setText(item.getReferenceText());
      setGraphic(root);
    }
  }
}
