package uk.ramp.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.reader.Reader;
import uk.ramp.yaml.YamlReader;

class MetaDataReader implements Reader<List<ImmutableMetadataItem>> {
  private static final String LOCATION = "metadata.yaml";
  private final YamlReader yamlReader;
  private final Path absoluteLocationPath;

  MetaDataReader(YamlReader yamlReader, FileDirectoryNormaliser fileDirectoryNormaliser) {
    this.yamlReader = yamlReader;
    this.absoluteLocationPath = Path.of(fileDirectoryNormaliser.normalisePath(LOCATION));
  }

  @Override
  public List<ImmutableMetadataItem> read() {
    try {
      return yamlReader.read(
          Files.newBufferedReader(absoluteLocationPath), new TypeReference<>() {});
    } catch (IOException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }
}
