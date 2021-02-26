package de.fxnn.geld;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class Mt940ParserTest {

  private static final String SOURCE_RESOURCE_NAME = "mt940.yml";

  @ParameterizedTest
  @MethodSource("loadMt940Cases")
  void parse(Mt940Case mt940Case) throws IOException {
    List<Mt940Field> fields = whenSutParses(mt940Case);
    assertThat(fields, hasSize(greaterThan(0)));
  }

  @Test
  void parse__exampleSwiftMt940Domestic1() throws IOException {
    var mt940Case = loadMt940Case("exampleSwiftMt940Domestic1");
    var fields = whenSutParses(mt940Case);
    assertThat(fields.get(0).getTag(), is("1"));
  }

  private List<Mt940Field> whenSutParses(Mt940Case mt940Case) throws IOException {
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
