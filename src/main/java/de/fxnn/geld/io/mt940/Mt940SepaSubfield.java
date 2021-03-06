package de.fxnn.geld.io.mt940;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * These fields are encoded in {@link Mt940InformationField} subfields. They provide additional
 * semantics in the existing MT940 datastructure and usually only apply when {@link
 * Mt940InformationField#isSepaTransfer()} is {@code true}.
 */
public enum Mt940SepaSubfield {
  END_TO_END_REFERENCE("EREF"),
  CUSTOMER_REFERENCE("KREF"),
  MANDATE_REFERENCE("MREF"),
  CREDITOR_ID("CRED"),
  DEBITOR_ID("DEBT"),
  REFERENCE_TEXT("SVWZ"),
  ULTIMATE_PRINCIPAL("ABWA"),
  ULTIMATE_BENEFICIARY("ABWE");

  private final String prefix;
  private static final Map<String, Mt940SepaSubfield> BY_PREFIX = createByPrefixMap();

  Mt940SepaSubfield(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public static Optional<Mt940SepaSubfield> ofPrefix(String prefix) {
    var result = BY_PREFIX.get(prefix);
    return Optional.ofNullable(result);
  }

  private static Map<String, Mt940SepaSubfield> createByPrefixMap() {
    return Stream.of(Mt940SepaSubfield.values())
        .collect(Collectors.toMap(Mt940SepaSubfield::getPrefix, Function.identity()));
  }
}
