package de.fxnn.geld.io.mt940;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Different types of fields in a {@link Mt940Message}. Note, that some, but not all field types are
 * mentioned here or are implemented as {@link Mt940Field} subclass.
 */
public enum Mt940FieldType {
  TEXT_BLOCK("4", Mt940MessageArea.HEADER),
  ACCOUNT("25", Mt940MessageArea.HEADER) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940AccountField.of(rawField);
    }
  },
  FINAL_OPENING_BALANCE("60F", Mt940MessageArea.HEADER) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940BalanceField.of(rawField);
    }
  },
  INTERMEDIATE_OPENING_BALANCE("60M", Mt940MessageArea.HEADER) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940BalanceField.of(rawField);
    }
  },
  TRANSACTION("61", Mt940MessageArea.TRANSACTION) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940TransactionField.of(rawField);
    }
  },
  INFORMATION("86", Mt940MessageArea.TRANSACTION) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940InformationField.of(rawField);
    }
  },
  FINAL_CLOSING_BALANCE("62F", Mt940MessageArea.FOOTER) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940BalanceField.of(rawField);
    }
  },
  INTERMEDIATE_CLOSING_BALANCE("62M", Mt940MessageArea.FOOTER) {
    @Override
    public Mt940Field create(Mt940RawField rawField) {
      return Mt940BalanceField.of(rawField);
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
  private final Mt940MessageArea messageArea;

  Mt940FieldType(String tag, Mt940MessageArea messageArea) {
    this.tag = tag;
    this.messageArea = messageArea;
  }

  public String getTag() {
    return tag;
  }

  public Mt940MessageArea getMessageArea() {
    return messageArea;
  }

  public Mt940Field create(Mt940RawField rawField) {
    throw new UnsupportedOperationException("No mapping implemented for " + this);
  }
}
