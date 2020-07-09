package uk.ramp.metadata;

import uk.ramp.hash.Hasher;

public class HashVerificationMetadataSelector implements MetadataSelector {
  private final MetadataSelector metadataSelector;
  private final Hasher hasher;
  private final boolean shouldVerifyHash;

  HashVerificationMetadataSelector(
      MetadataSelector metadataSelector, Hasher hasher, boolean shouldVerifyHash) {
    this.metadataSelector = metadataSelector;
    this.hasher = hasher;
    this.shouldVerifyHash = shouldVerifyHash;
  }

  @Override
  public MetadataItem find(MetadataItem queryMetaDataItem) {
    var readMetaDataItem = metadataSelector.find(queryMetaDataItem);

    if (readMetaDataItem.verifiedHash().isEmpty()) {
      return readMetaDataItem;
    }

    if (readMetaDataItem.filename().isEmpty()) {
      throw new IllegalStateException("Filename not specified. " + readMetaDataItem);
    }

    var verifiedHash = readMetaDataItem.verifiedHash().get();

    String calculatedHash = hasher.hash(readMetaDataItem.filename().get());

    if (shouldVerifyHash && !calculatedHash.equals(verifiedHash)) {
      throw new IllegalStateException(
          String.format(
              "Verified hash %s does not match calculated hash %s", verifiedHash, calculatedHash));
    }

    return ImmutableMetadataItem.copyOf(readMetaDataItem).withCalculatedHash(calculatedHash);
  }
}
