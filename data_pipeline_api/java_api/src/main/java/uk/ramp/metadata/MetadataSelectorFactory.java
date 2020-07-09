package uk.ramp.metadata;

import java.util.ArrayList;
import java.util.List;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.hash.Hasher;
import uk.ramp.yaml.YamlReader;

public class MetadataSelectorFactory {
  public MetadataSelector metadataSelector(
      Hasher hasher,
      YamlReader yamlReader,
      boolean shouldVerifyHash,
      FileDirectoryNormaliser fileDirectoryNormaliser) {
    List<MetadataItem> metadataItems =
        new ArrayList<>(new MetaDataReader(yamlReader, fileDirectoryNormaliser).read());
    MetadataSelector baseMetadataSelector = new MatchingMetadataSelector(metadataItems);
    return new HashVerificationMetadataSelector(baseMetadataSelector, hasher, shouldVerifyHash);
  }
}
