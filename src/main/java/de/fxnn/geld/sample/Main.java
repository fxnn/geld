package de.fxnn.geld.sample;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import de.fxnn.geld.io.mt940.Mt940BalanceField;
import de.fxnn.geld.io.mt940.Mt940Message.Transaction;
import de.fxnn.geld.io.mt940.Mt940MessageParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends MobileApplication {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  @Override
  public void init() {
    addViewFactory(
        HOME_VIEW,
        () -> {
          ObservableList<TransactionView> transactionList = FXCollections.observableArrayList();

          FloatingActionButton fab =
              new FloatingActionButton(
                  MaterialDesignIcon.FOLDER_OPEN.text,
                  e -> {
                    var fileChooser = new FileChooser();
                    var file = fileChooser.showOpenDialog(getView().getScene().getWindow());
                    if (file != null) {
                      transactionList.clear();
                      transactionList.addAll(loadTransactionViews(file));
                    }
                  });

          var list = new CharmListView<>(transactionList);
          list.setCellFactory(
              listView ->
                  new CharmListCell<>() {

                    private final TransactionListCellController listCellController =
                        new TransactionListCellController();

                    {
                      prefWidthProperty().bind(list.widthProperty());
                      setMaxWidth(USE_PREF_SIZE);
                    }

                    @Override
                    public void updateItem(TransactionView item, boolean empty) {
                      super.updateItem(item, empty);
                      if (empty) {
                        setGraphic(null);
                      } else {
                        listCellController.setItem(item);
                        setGraphic(listCellController.getView());
                      }
                    }
                  });

          View view =
              new View(list) {
                @Override
                protected void updateAppBar(AppBar appBar) {
                  appBar.setTitleText("Geld");
                }
              };

          fab.showOn(view);

          return view;
        });
  }

  private List<TransactionView> loadTransactionViews(File file) {
    var result = new ArrayList<TransactionView>();

    try (var reader =
        new LineNumberReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
      var parser = new Mt940MessageParser(reader);
      try {
        while (parser.hasNext()) {
          var message = parser.next();
          var balanceBefore = message.getHeader(Mt940BalanceField.class).getAmountAsBigDecimal();
          for (Transaction tx : message.getTransactions()) {
            var infoField = tx.getInformationField();
            var txField = tx.getTransactionField();

            var txView = new TransactionView();
            txView.setBalanceBefore(balanceBefore);
            txView.setAmount(txField.getAmountAsBigDecimal());
            txView.setTransactionDescription(infoField.getTransactionDescription());
            txView.setReferenceText(
                Optional.ofNullable(infoField.getSepaReferenceText())
                    .orElse(infoField.getReferenceText()));
            txView.setBeneficiary(
                Optional.ofNullable(infoField.getSepaUltimatePrincipal())
                    .orElse(infoField.getBeneficiaryName()));
            balanceBefore = balanceBefore.add(txField.getAmountAsBigDecimal());
            txView.setBalanceAfter(balanceBefore);

            result.add(txView);
          }
        }
      } catch (Exception e) {
        LOG.warn("error in file '{}' at line {}", file, reader.getLineNumber(), e);
      }
    } catch (IOException e) {
      LOG.warn("could not read the file '{}'", file, e);
    }

    return result;
  }

  @Override
  public void postInit(Scene scene) {
    Swatch.LIGHT_GREEN.assignTo(scene);
    scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());

    if (Platform.isDesktop()) {
      Dimension2D dimension2D =
          DisplayService.create()
              .map(DisplayService::getDefaultDimensions)
              .orElse(new Dimension2D(640, 480));
      scene.getWindow().setWidth(dimension2D.getWidth());
      scene.getWindow().setHeight(dimension2D.getHeight());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
