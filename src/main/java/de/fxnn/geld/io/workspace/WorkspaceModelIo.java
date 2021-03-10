package de.fxnn.geld.io.workspace;

import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class WorkspaceModelIo {

  private final ExternalWorkspaceMapper mapper = ExternalWorkspaceMapper.create();
  private final ExternalWorkspaceIo io = new ExternalWorkspaceIo();

  public WorkspaceModel load(Path path) throws IOException {
    return mapper.toInternal(io.load(path));
  }

  public void store(WorkspaceModel object, Path path, OpenOption... options) throws IOException {
    io.store(mapper.toExternal(object), path, options);
  }
}
