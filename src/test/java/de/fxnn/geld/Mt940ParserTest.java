package de.fxnn.geld;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class Mt940ParserTest {

  private static final String SOURCE_RESOURCE_NAME = "mt940.yml";

  @ParameterizedTest
  @MethodSource("loadMt940Cases")
  void parse(Mt940Case mt940Case) throws IOException {
    List<Mt940RawField> fields = whenSutParses(mt940Case);
    assertThat(fields, hasSize(greaterThan(0)));
  }

  @Test
  void parse__exampleSwiftMt940Domestic1() throws IOException {
    var mt940Case = loadMt940Case("exampleSwiftMt940Domestic1");
    var fields = whenSutParses(mt940Case);
    var iterator = fields.iterator();
    var field = iterator.next();
    assertThat(field.getTag(), is("1"));
    assertThat(field.getContent(), is("F01BPHKPLPKXXX0000000000"));
    field = iterator.next();
    assertThat(field.getTag(), is("2"));
    assertThat(field.getContent(), is("I940BOFAUS6BXBAMN"));
    field = iterator.next();
    assertThat(field.getTag(), is("4"));
    assertThat(field.getContent(), startsWith(":20:TELEWIZORY S.A."));
    assertThat(field.getContent(), endsWith(":62F:C020325PLN50040,00\n-"));
    assertFalse(iterator.hasNext());
  }

  @Test
  void parse__exampleSwiftMt940Sparkasse1() throws IOException {
    var mt940Case = loadMt940Case("exampleSwiftMt940Sparkasse1");
    var fields = whenSutParses(mt940Case);

    var iterator = fields.iterator();
    var field = iterator.next();
    assertThat(field.getTag(), is("20"));
    assertThat(field.getContent(), is("STARTUMSE"));
    field = iterator.next();
    assertThat(field.getTag(), is("25"));
    assertThat(field.getContent(), is("83050000/0123456789"));
    field = iterator.next();
    assertThat(field.getTag(), is("28C"));
    assertThat(field.getContent(), is("00000/001"));
    field = iterator.next();
    assertThat(field.getTag(), is("60F"));
    assertThat(field.getContent(), is("C161122EUR227,77"));
    field = iterator.next();
    assertThat(field.getTag(), is("61"));
    assertThat(field.getContent(), is("1611231123DR106,75N010NONREF"));
    field = iterator.next();
    assertThat(field.getTag(), is("86"));
    assertThat(field.getContent(), is("006"
        + "?00EIGENE KREDITKARTENABRECHN."
        + "?101234"
        + "?20KREDITKARTEN-ABR.XVOM 18.11"
        + "?21447595XXXXXX7209     106,75"
        + "?3083050000"
        + "?310123456789"
        + "?32SPARKASSE GERA-GREIZ"
        + "?34123"));
    field = iterator.next();
    assertThat(field.getTag(), is("62F"));
    assertThat(field.getContent(), is("C161123EUR121,02"));
    assertFalse(iterator.hasNext());
  }

  private List<Mt940RawField> whenSutParses(Mt940Case mt940Case) throws IOException {
    return Mt940Parser.parse(mt940Case.getData());
  }

  static Mt940Case loadMt940Case(String id) throws IOException {
    return loadMt940Cases()
        .filter(c -> id.equals(c.getId()))
        .findAny()
        .orElseThrow(() -> new AssertionError("No such case: " + id));
  }

  static Stream<Mt940Case> loadMt940Cases() throws IOException {
    try (InputStream is = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(SOURCE_RESOURCE_NAME)) {
      var mapper = new ObjectMapper(new YAMLFactory());
      return mapper.readValue(is, new TypeReference<List<Mt940Case>>() {
      }).stream();
    }
  }
}
