package de.fxnn.geld.io.workspace;

import com.dslplatform.json.CompiledJson;
import java.util.List;
import lombok.Value;

@Value
@CompiledJson
public class ExternalWorkspace {

  List<ExternalCategory> categoryList;
  List<ExternalTransaction> transactionList;
}
