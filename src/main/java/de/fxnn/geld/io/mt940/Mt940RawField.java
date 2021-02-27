package de.fxnn.geld.io.mt940;

import lombok.Value;

/**
 * @see <a href="http://martin.hinner.info/bankconvert/swift_mt940_942.pdf">MT940 format
 *     description</a>
 */
@Value
public class Mt940RawField implements Mt940Field {

  /** Identifier of the field as given by spec. Defines how to parse the {@link #rawContent}. */
  String tag;

  /** Raw data as given by the MT940 source. */
  String rawContent;
}
