package de.fxnn.geld.jfx;

import static org.junit.jupiter.api.Assertions.*;

import de.fxnn.geld.application.platform.ApplicationMetadata;
import org.junit.jupiter.api.Test;

class ApplicationMetadataTest {

  @Test
  void nonNull() {
    var applicationMetadata = ApplicationMetadata.create();
    assertNotNull(applicationMetadata.getMavenGroupId());
    assertNotNull(applicationMetadata.getMavenArtifactId());
    assertNotNull(applicationMetadata.getMavenVersion());
  }
}
