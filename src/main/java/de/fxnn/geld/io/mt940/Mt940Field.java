package de.fxnn.geld.io.mt940;

import java.util.Optional;

/**
 * A field inside a {@link Mt940Message}. Concrete subclasses model different types of fields, see
 * {@link Mt940FieldType}.
 */
public interface Mt940Field {

  /** Identifier of the field as given by spec. */
  String getTag();

  default Optional<Mt940FieldType> getFieldType() {
    return Mt940FieldType.ofTag(getTag());
  }

  default boolean isTransactionalField() {
    return isMessageArea(Mt940MessageArea.TRANSACTION);
  }

  default boolean isHeaderField() {
    return isMessageArea(Mt940MessageArea.HEADER);
  }

  default boolean isFooterField() {
    return isMessageArea(Mt940MessageArea.FOOTER);
  }

  private boolean isMessageArea(Mt940MessageArea transaction) {
    return getFieldType()
        .map(Mt940FieldType::getMessageArea)
        .filter(transaction::equals)
        .isPresent();
  }

  default boolean isTransactionBegin() {
    return getFieldType().filter(Mt940FieldType.TRANSACTION::equals).isPresent();
  }
}
