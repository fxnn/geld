package de.fxnn.geld.io.mt940;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class Mt940AccountField extends SimpleMt940Field {

  /**
   * Interpretation of this field is specific to the bank.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>IBAN
   *   <li>BIC and account number
   * </ul>
   */
  String accountNumber;

  public Mt940AccountField(String tag, String accountNumber) {
    super(tag);
    this.accountNumber = accountNumber;
  }

  public static Mt940Field of(Mt940RawField rawField) {
    return new Mt940AccountField(rawField.getTag(), rawField.getRawContent());
  }
}
