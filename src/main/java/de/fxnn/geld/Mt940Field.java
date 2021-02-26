package de.fxnn.geld;

import lombok.Value;

/**
 * @see <a href="http://martin.hinner.info/bankconvert/swift_mt940_942.pdf">MT940 format
 * description</a>
 */
@Value
public class Mt940Field {

  /**
   * Numeric identifier of the field.
   */
  private String tag;

}
