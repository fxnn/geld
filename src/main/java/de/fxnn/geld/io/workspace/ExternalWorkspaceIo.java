package de.fxnn.geld.io.workspace;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.DslJson.Settings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ExternalWorkspaceIo {

  private static final DslJson<Object> DSL_JSON =
      new DslJson<>(
          new Settings<>().includeServiceLoader(ExternalWorkspaceIo.class.getClassLoader()));

  public ExternalWorkspace load(Path path) throws IOException {
    try (var is = Files.newInputStream(path, StandardOpenOption.READ)) {
      return DSL_JSON.deserialize(ExternalWorkspace.class, is);
    }
  }

  public void store(ExternalWorkspace object, Path path, OpenOption... options) throws IOException {
    try (var os = Files.newOutputStream(path, options)) {
      DSL_JSON.serialize(object, os);
    }
  }
}
