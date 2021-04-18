package de.fxnn.geld.common.view;

import static de.fxnn.geld.common.platform.I18n.i18n;

import com.gluonhq.charm.glisten.control.Alert;
import com.google.common.base.Throwables;
import java.util.Objects;
import javafx.scene.control.Alert.AlertType;

public class AlertFactory {

  public Alert createError(String message, Throwable throwable) {
    return new Alert(AlertType.ERROR, message + createThrowableMessage(throwable));
  }

  public Alert createWarning(String message, Throwable throwable) {
    return new Alert(AlertType.WARNING, message + createThrowableMessage(throwable));
  }

  private String createThrowableMessage(Throwable throwable) {
    if (throwable == null) {
      return "";
    }

    var result =
        i18n()
            .formatMessage(
                "alert.cause.direct", throwable.getClass().getName(), throwable.getMessage());

    var rootCause = Throwables.getRootCause(throwable);
    if (!Objects.equals(rootCause, throwable)) {
      result +=
          i18n()
              .formatMessage(
                  "alert.cause.root", rootCause.getClass().getName(), rootCause.getMessage());
    }

    return result;
  }
}
