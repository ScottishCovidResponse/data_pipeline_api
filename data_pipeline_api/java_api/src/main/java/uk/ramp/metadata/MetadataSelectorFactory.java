package uk.ramp.metadata;

import java.util.ArrayList;
import java.util.List;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.yaml.YamlReader;

public class MetadataSelectorFactory {
  public MetadataSelector metadataSelector(
      YamlReader yamlReader, FileDirectoryNormaliser fileDirectoryNormaliser) {
    List<MetadataItem> metadataItems =
        new ArrayList<>(new MetaDataReader(yamlReader, fileDirectoryNormaliser).read());
    return new MatchingMetadataSelector(metadataItems);
  }
}
