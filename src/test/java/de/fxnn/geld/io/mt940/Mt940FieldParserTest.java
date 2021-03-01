package de.fxnn.geld.io.mt940;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class Mt940FieldParserTest {

  @ParameterizedTest
  @MethodSource("de.fxnn.geld.io.mt940.Mt940Case#loadAll")
  void parse(Mt940Case mt940Case) throws IOException {
    var sut = createSut(mt940Case);

    int count = 0;
    while (sut.hasNext()) {
      var field = sut.next();
      assertNotNull(field);
      count++;
    }

    assertThat(count, is(greaterThan(0)));
  }

  @Test
  void parse__exampleSwiftMt940Domestic1() throws IOException {
    var mt940Case = Mt940Case.load("exampleSwiftMt940Domestic1");
    var sut = createSut(mt940Case);

    var field = assertHasNext(sut);
    assertFieldIsSimple(field, "1");

    field = assertHasNext(sut);
    assertFieldIsSimple(field, "2");

    field = assertHasNext(sut);
    assertFieldIsSimple(field, "20");

    field = assertHasNext(sut);
    var accountField = assertFieldIsOfType(field, "25", Mt940AccountField.class);
    assertEquals("BPHKPLPK/320000546101", accountField.getAccountNumber());

    field = assertHasNext(sut);
    assertFieldIsSimple(field, "28C");

    field = assertHasNext(sut);
    var balanceField = assertFieldIsOfType(field, "60F", Mt940BalanceField.class);
    assertEquals(Currency.getInstance("PLN"), balanceField.getCurrency());
    assertEquals(4000000, balanceField.getAmount());
  }

  private Mt940Field assertHasNext(Mt940FieldParser sut) throws IOException {
    assertTrue(sut.hasNext());
    return sut.next();
  }

  private <T extends Mt940Field> T assertFieldIsOfType(
      Mt940Field field, String expectedTag, Class<T> expectedType) {
    assertEquals(expectedTag, field.getTag());
    assertThat(field, is(instanceOf(expectedType)));
    return expectedType.cast(field);
  }

  private void assertFieldIsSimple(Mt940Field field, String expectedTag) {
    assertEquals(expectedTag, field.getTag());
    assertThat(field, is(instanceOf(SimpleMt940Field.class)));
  }

  private Mt940FieldParser createSut(Mt940Case mt940Case) {
    return new Mt940FieldParser(new StringReader(mt940Case.getData()));
  }
}
