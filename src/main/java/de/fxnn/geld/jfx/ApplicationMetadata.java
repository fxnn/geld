package de.fxnn.geld.jfx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

@RequiredArgsConstructor
public class ApplicationMetadata {

  private static final String METADATA_PROPERTIES_RESOURCE_NAME = "metadata.properties";
  private static final String MAVEN_GROUP_ID_KEY = "app.maven.groupId";
  private static final String MAVEN_ARTIFACT_ID_KEY = "app.maven.artifactId";
  private static final String MAVEN_VERSION_KEY = "app.maven.version";

  @Getter private final String mavenGroupId;
  @Getter private final String mavenArtifactId;
  @Getter private final String mavenVersion;
  private final AppDirs appDirs = AppDirsFactory.getInstance();

  public Path getAppConfigPath() {
    // HINT: we don't want the path to depend on the version number
    return Path.of(appDirs.getUserDataDir(mavenArtifactId, null, mavenGroupId));
  }

  public static ApplicationMetadata create() {
    try {
      Properties properties = new Properties();
      properties.load(
          ApplicationMetadata.class.getResourceAsStream(METADATA_PROPERTIES_RESOURCE_NAME));
      return new ApplicationMetadata(
          properties.getProperty(MAVEN_GROUP_ID_KEY),
          properties.getProperty(MAVEN_ARTIFACT_ID_KEY),
          properties.getProperty(MAVEN_VERSION_KEY));
    } catch (IOException ex) {
      throw new IllegalStateException("Application Metadata could not be read", ex);
    }
  }
}
