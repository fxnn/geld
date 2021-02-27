package de.fxnn.geld.io.mt940;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
public class Mt940Case {

  private static final String SOURCE_RESOURCE_NAME = "mt940.yml";

  @ToString.Include private String id;
  private Map<String, Object> metadata;
  private String data;

  static Mt940Case load(String id) throws IOException {
    return loadAll()
        .filter(c -> id.equals(c.getId()))
        .findAny()
        .orElseThrow(() -> new AssertionError("No such case: " + id));
  }

  static Stream<Mt940Case> loadAll() throws IOException {
    try (InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(SOURCE_RESOURCE_NAME)) {
      var mapper = new ObjectMapper(new YAMLFactory());
      return mapper.readValue(is, new TypeReference<List<Mt940Case>>() {}).stream();
    }
  }
}
