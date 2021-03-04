package de.fxnn.geld.io.mt940;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Mt940SepaField {
  END_TO_END_REFERENCE("EREF"),
  CUSTOMER_REFERENCE("KREF"),
  MANDATE_REFERENCE("MREF"),
  CREDITOR_ID("CRED"),
  DEBITOR_ID("DEBT"),
  REFERENCE_TEXT("SVWZ"),
  ULTIMATE_PRINCIPAL("ABWA"),
  ULTIMATE_BENEFICIARY("ABWE");

  private final String prefix;
  private static final Map<String, Mt940SepaField> BY_PREFIX = createByPrefixMap();

  Mt940SepaField(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public static Optional<Mt940SepaField> ofPrefix(String prefix) {
    var result = BY_PREFIX.get(prefix);
    return Optional.ofNullable(result);
  }

  private static Map<String, Mt940SepaField> createByPrefixMap() {
    return Stream.of(Mt940SepaField.values())
        .collect(Collectors.toMap(Mt940SepaField::getPrefix, Function.identity()));
  }
}
