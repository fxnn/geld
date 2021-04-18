package de.fxnn.geld.common.platform;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

  private static final I18n INSTANCE = new I18n(Locale.getDefault());
  private static final String RESOURCE_BUNDLE_NAME = "Messages";

  private final ResourceBundle resourceBundle;
  private final Locale locale;

  private I18n(Locale locale) {
    resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
    this.locale = locale;
  }

  public String formatMessage(String key, Object... args) {
    return MessageFormat.format(message(key), args);
  }

  public String message(String key) {
    return resourceBundle.getString(key);
  }

  public Locale getLocale() {
    return locale;
  }

  public static I18n i18n() {
    return INSTANCE;
  }
}
