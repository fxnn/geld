package de.fxnn.geld;

import lombok.Value;

/**
 * @see <a href="http://martin.hinner.info/bankconvert/swift_mt940_942.pdf">MT940 format
 * description</a>
 */
@Value
public class Mt940RawField {

  /**
   * Identifier of the field as given by spec. Defines how to parse the {@link #content}.
   */
  private String tag;
  /**
   * Raw data contained in the field.
   */
  private String content;

}
