package de.fxnn.geld.io.mt940;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class Mt940RawFieldParserTest {

  @ParameterizedTest
  @MethodSource("de.fxnn.geld.io.mt940.Mt940Case#loadAll")
  void parse(Mt940Case mt940Case) throws IOException {
    var parser = new Mt940RawFieldParser(new StringReader(mt940Case.getData()));

    int count = 0;
    while (parser.hasNext()) {
      var field = parser.next();
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
    assertThat(field.getTag(), is("1"));
    assertThat(field.getRawContent(), is("F01BPHKPLPKXXX0000000000"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("2"));
    assertThat(field.getRawContent(), is("I940BOFAUS6BXBAMN"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("4"));
    assertThat(field.getRawContent(), startsWith(":20:TELEWIZORY S.A."));
    assertThat(field.getRawContent(), endsWith(":62F:C020325PLN50040,00\n-"));

    assertFalse(sut.hasNext());
  }

  @Test
  void parse__exampleSwiftMt940Sparkasse1() throws IOException {
    var mt940Case = Mt940Case.load("exampleSwiftMt940Sparkasse1");
    var sut = createSut(mt940Case);

    assertTrue(sut.hasNext());
    var field = sut.next();
    assertThat(field.getTag(), is("20"));
    assertThat(field.getRawContent(), is("STARTUMSE"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("25"));
    assertThat(field.getRawContent(), is("83050000/0123456789"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("28C"));
    assertThat(field.getRawContent(), is("00000/001"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("60F"));
    assertThat(field.getRawContent(), is("C161122EUR227,77"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("61"));
    assertThat(field.getRawContent(), is("1611231123DR106,75N010NONREF"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("86"));
    assertThat(
        field.getRawContent(),
        is(
            "006"
                + "?00EIGENE KREDITKARTENABRECHN."
                + "?101234"
                + "?20KREDITKARTEN-ABR.XVOM 18.11"
                + "?21447595XXXXXX7209     106,75"
                + "?3083050000"
                + "?310123456789"
                + "?32SPARKASSE GERA-GREIZ"
                + "?34123"));

    assertTrue(sut.hasNext());
    field = sut.next();
    assertThat(field.getTag(), is("62F"));
    assertThat(field.getRawContent(), is("C161123EUR121,02"));

    assertFalse(sut.hasNext());
  }

  private Mt940RawFieldParser createSut(Mt940Case mt940Case) {
    return new Mt940RawFieldParser(new StringReader(mt940Case.getData()));
  }
}
