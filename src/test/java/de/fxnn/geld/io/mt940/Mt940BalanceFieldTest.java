package de.fxnn.geld.io.mt940;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import de.fxnn.geld.io.mt940.Mt940BalanceField.BalanceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Mt940BalanceFieldTest {

  private static Stream<Arguments> rawAndParsedValues() {
    return Stream.of(
        arguments(
            "C031002PLN40000,00",
            BalanceType.CREDIT,
            LocalDate.of(2003, 10, 2),
            Currency.getInstance("PLN"),
            4000000L),
        arguments(
            "C031209PLN95,03",
            BalanceType.CREDIT,
            LocalDate.of(2003, 12, 9),
            Currency.getInstance("PLN"),
            9503L),
        arguments(
            "C161122EUR227,77",
            BalanceType.CREDIT,
            LocalDate.of(2016, 11, 22),
            Currency.getInstance("EUR"),
            22777L),
        arguments(
            "D800131DEM99,99",
            BalanceType.DEBIT,
            LocalDate.of(1980, 1, 31),
            Currency.getInstance("DEM"),
            9999L));
  }

  @ParameterizedTest
  @MethodSource("rawAndParsedValues")
  void success(
      String rawContent, BalanceType balanceType, LocalDate date, Currency currency, long amount) {
    var sut = createSut(rawContent);
    assertEquals(balanceType, sut.getBalanceType());
    assertEquals(date, sut.getDate());
    assertEquals(currency, sut.getCurrency());
    assertEquals(amount, sut.getAmount());
  }

  @Test
  void illegalContent() {
    assertThrows(IllegalArgumentException.class, () -> createSut(""));
  }

  @Test
  void getAmountAsBigDecimal__positive() {
    var sut = createSut("C990101EUR567,89");
    assertEquals(new BigDecimal("567.89"), sut.getAmountAsBigDecimal());
  }

  @Test
  void getAmountAsBigDecimal__negative() {
    var sut = createSut("D990101EUR543,21");
    assertEquals(new BigDecimal("-543.21"), sut.getAmountAsBigDecimal());
  }

  @Test
  void isIntermediate__true() {
    var sut = Mt940BalanceField.of(new Mt940RawField("60M", "C031002PLN40000,00"));
    assertTrue(sut.isIntermediate());
  }

  @Test
  void isIntermediate__false() {
    var sut = Mt940BalanceField.of(new Mt940RawField("60F", "C031002PLN40000,00"));
    assertFalse(sut.isIntermediate());
  }

  private Mt940BalanceField createSut(String rawContent) {
    return Mt940BalanceField.of(new Mt940RawField("60F", rawContent));
  }
}
