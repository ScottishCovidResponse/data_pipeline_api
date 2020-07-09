package uk.ramp.config;

import java.time.Instant;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.file.FileReader;
import uk.ramp.hash.HasherFactory;
import uk.ramp.yaml.YamlReader;

public class ConfigFactory {
  private static final String LOCATION = "config.yaml";

  public Config config(
      YamlReader yamlReader,
      HasherFactory hasherFactory,
      FileReader fileReader,
      Instant openTimestamp,
      FileDirectoryNormaliser fileDirectoryNormaliser) {
    var config = new ConfigReader(yamlReader, fileDirectoryNormaliser).read();
    var failOnHashMismatch = config.failOnHashMisMatch().orElse(true);
    var freshHash =
        hasherFactory
            .fileHasher(fileReader, openTimestamp)
            .hash(fileDirectoryNormaliser.normalisePath(LOCATION));
    var runId = config.runId().orElse(freshHash);
    return config
        .withFailOnHashMisMatch(failOnHashMismatch)
        .withRunId(runId)
        .withParentPath(fileDirectoryNormaliser.parentPath());
  }
}
