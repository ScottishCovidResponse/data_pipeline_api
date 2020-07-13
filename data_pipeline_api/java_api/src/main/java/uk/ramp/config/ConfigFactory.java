package uk.ramp.config;

import java.nio.file.Path;
import java.time.Instant;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.hash.Hasher;
import uk.ramp.yaml.YamlReader;

public class ConfigFactory {
  private static final String LOCATION = "config.yaml";

  public Config config(
      YamlReader yamlReader,
      Hasher hasher,
      Instant openTimestamp,
      FileDirectoryNormaliser fileDirectoryNormaliser) {
    var normalisedPath = Path.of(fileDirectoryNormaliser.normalisePath(LOCATION));
    var config = new ConfigReader(yamlReader, normalisedPath).read();
    var failOnHashMismatch = config.failOnHashMisMatch().orElse(true);
    var freshHash = hasher.fileHash(fileDirectoryNormaliser.normalisePath(LOCATION), openTimestamp);
    var runId = config.runId().orElse(freshHash);
    return config
        .withFailOnHashMisMatch(failOnHashMismatch)
        .withRunId(runId)
        .withParentPath(fileDirectoryNormaliser.parentPath());
  }
}
