package de.fxnn.geld.io.mt940;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public enum Mt940FieldType {
  TEXT_BLOCK("4"),
  ACCOUNT("25") {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940AccountField.of(rawField);
    }
  };

  private static final Map<String, Mt940FieldType> BY_TAG = createByTagMap();

  private static Map<String, Mt940FieldType> createByTagMap() {
    return Stream.of(Mt940FieldType.values())
        .collect(toMap(Mt940FieldType::getTag, Function.identity()));
  }

  public static Optional<Mt940FieldType> ofTag(String tag) {
    return Optional.ofNullable(BY_TAG.get(tag));
  }

  private final String tag;

  Mt940FieldType(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  public Mt940Field create(Mt940RawField rawField) {
    throw new UnsupportedOperationException("No mapping implemented for " + this);
  }
}
