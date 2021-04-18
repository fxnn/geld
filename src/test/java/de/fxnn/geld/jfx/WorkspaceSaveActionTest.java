package de.fxnn.geld.jfx;

import com.google.common.jimfs.Jimfs;
import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.application.platform.ApplicationMetadata;
import de.fxnn.geld.application.view.WorkspaceSaveAction;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WorkspaceSaveActionTest {

  @Test
  void createsWorkspaceFile() {
    var fs = givenFileSystem();
    var nonExistantDirectory = fs.getPath("testDir");
    var applicationMetadata = givenAppMetadataWithConfigPath(nonExistantDirectory);

    var sut = new WorkspaceSaveAction(new WorkspaceModel(), applicationMetadata);
    sut.run();

    Assertions.assertTrue(
        Files.exists(nonExistantDirectory.resolve(WorkspaceSaveAction.WORKSPACE_JSON_FILENAME)));
  }

  private ApplicationMetadata givenAppMetadataWithConfigPath(Path path) {
    var applicationMetadata = Mockito.mock(ApplicationMetadata.class);
    Mockito.when(applicationMetadata.getAppConfigPath()).thenReturn(path);
    return applicationMetadata;
  }

  private FileSystem givenFileSystem() {
    return Jimfs.newFileSystem();
  }
}
