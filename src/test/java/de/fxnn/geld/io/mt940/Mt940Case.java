package de.fxnn.geld.io.mt940;

import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
public class Mt940Case {

  @ToString.Include private String id;
  private Map<String, Object> metadata;
  private String data;
}
