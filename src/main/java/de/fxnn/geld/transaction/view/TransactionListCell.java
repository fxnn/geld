package de.fxnn.geld.transaction.view;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class TransactionListCell extends CharmListCell<TransactionModel> {

  private static final String LIST_CELL_VBOX_ID = "TransactionListCellVBox";
  private static final String AMOUNT_POSITIVE_CLASS = "positiveAmount";
  private static final String AMOUNT_NEGATIVE_CLASS = "negativeAmount";
  private static final double EXPECTED_BORDER_AND_SCROLL_BAR_WIDTH = 25.0;

  private final Label amount = new Label();
  private final Label beneficiary = new Label();
  private final Label referenceText = new Label();
  private final HBox keyLine = new HBox(amount, beneficiary);
  private final VBox lines = new VBox(keyLine, referenceText);
  private final FontIcon categoryIcon = new FontIcon();
  private final HBox root = new HBox(lines, categoryIcon);

  public TransactionListCell(CharmListView<TransactionModel, LocalDate> listView) {
    prefWidthProperty()
        .bind(listView.widthProperty().subtract(EXPECTED_BORDER_AND_SCROLL_BAR_WIDTH));
    setMaxWidth(USE_PREF_SIZE);

    amount.setPrefWidth(70.0d);
    amount.setMinWidth(Region.USE_PREF_SIZE);
    amount.setAlignment(Pos.BASELINE_RIGHT);
    amount.getStyleClass().add(AMOUNT_POSITIVE_CLASS);
    HBox.setHgrow(beneficiary, Priority.ALWAYS);

    referenceText.setTextOverrun(OverrunStyle.ELLIPSIS);
    keyLine.setSpacing(10.0d);

    categoryIcon.setIconSize(32);

    lines.setId(LIST_CELL_VBOX_ID);
    HBox.setHgrow(lines, Priority.ALWAYS);

    root.maxWidthProperty().bind(prefWidthProperty());
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
      categoryIcon.setIconCode(item.getCategoryIkon());
      setGraphic(root);
    }
  }
}
