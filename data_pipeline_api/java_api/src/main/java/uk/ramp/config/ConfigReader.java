package uk.ramp.config;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.reader.Reader;
import uk.ramp.yaml.YamlReader;

class ConfigReader implements Reader<ImmutableConfig> {
  private static final String LOCATION = "config.yaml";

  private final YamlReader yamlReader;
  private final Path absoluteLocationPath;

  ConfigReader(YamlReader yamlReader, FileDirectoryNormaliser fileDirectoryNormaliser) {
    this.absoluteLocationPath = Path.of(fileDirectoryNormaliser.normalisePath(LOCATION));
    this.yamlReader = yamlReader;
  }

  @Override
  public ImmutableConfig read() {
    try {
      return yamlReader.read(
          Files.newBufferedReader(absoluteLocationPath), new TypeReference<ImmutableConfig>() {});
    } catch (IOException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }
}
