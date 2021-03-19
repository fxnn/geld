package de.fxnn.geld.io.workspace;

import com.dslplatform.json.CompiledJson;
import lombok.Value;

@Value
@CompiledJson
public class ExternalCategory {

  String categoryIconName;
  String filterExpression;
}
