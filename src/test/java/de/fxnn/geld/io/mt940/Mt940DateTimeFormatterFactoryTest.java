package de.fxnn.geld.io.mt940;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Mt940DateTimeFormatterFactoryTest {

  @ParameterizedTest
  @CsvSource(
      delimiter = ' ',
      value = {"000102 2000-01-02", "991231 1999-12-31", "800101 1980-01-01", "790101 2079-01-01"})
  void parse(String given, String expectedIso8601) {
    LocalDate expected = LocalDate.parse(expectedIso8601);
    var actual = new Mt940DateTimeFormatterFactory().create().parse(given, LocalDate::from);
    Assertions.assertEquals(expected, actual);
  }
}
