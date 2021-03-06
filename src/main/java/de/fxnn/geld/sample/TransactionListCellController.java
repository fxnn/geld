package de.fxnn.geld.sample;

import java.math.BigDecimal;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class TransactionListCellController {

  @Setter private TransactionView item;

  private final Label amount = new Label();
  private final Label beneficiary = new Label();
  private final Label referenceText = new Label();
  private final HBox keyLine = new HBox();
  private final VBox root = new VBox();

  public TransactionListCellController() {
    amount.setPrefWidth(70.0d);
    amount.setMinWidth(Region.USE_PREF_SIZE);
    amount.setAlignment(Pos.BASELINE_RIGHT);
    amount.getStyleClass().add("positiveAmount");
    keyLine.getChildren().addAll(amount, beneficiary);
    keyLine.setSpacing(10.0d);
    HBox.setHgrow(amount, Priority.ALWAYS);
    root.getChildren().addAll(keyLine, referenceText);
    root.setId("TransactionListCellVBox");
  }

  public Node getView() {
    amount.setText(item.getAmount().toString());
    if (item.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
      amount.getStyleClass().set(0, "positiveAmount");
    } else {
      amount.getStyleClass().set(0, "negativeAmount");
    }
    beneficiary.setText(item.getBeneficiary());
    referenceText.setText(item.getReferenceText());
    return root;
  }
}
