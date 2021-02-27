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

    assertTrue(sut.hasNext());
    var field = sut.next();
    assertEquals("1", field.getTag());

    assertTrue(sut.hasNext());
    field = sut.next();
    assertEquals("2", field.getTag());

    assertTrue(sut.hasNext());
    field = sut.next();
    assertEquals("20", field.getTag());

    assertTrue(sut.hasNext());
    field = sut.next();
    assertEquals("25", field.getTag());
    assertThat(field, is(instanceOf(Mt940AccountField.class)));
    assertEquals("BPHKPLPK/320000546101", ((Mt940AccountField) field).getAccountNumber());
  }

  private Mt940FieldParser createSut(Mt940Case mt940Case) {
    return new Mt940FieldParser(new StringReader(mt940Case.getData()));
  }
}
