package de.fxnn.geld.io.mt940;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class SimpleMt940Field implements Mt940Field {

  String tag;
}
