package de.fxnn.geld.io.mt940;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class Mt940DateTimeFormatterFactory {

  public DateTimeFormatter create() {
    return new DateTimeFormatterBuilder()
        .appendValueReduced(ChronoField.YEAR, 2, 2, 1980)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter();
  }
}
