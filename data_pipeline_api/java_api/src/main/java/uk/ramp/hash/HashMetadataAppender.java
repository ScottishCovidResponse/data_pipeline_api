package uk.ramp.hash;

import uk.ramp.metadata.ImmutableMetadataItem;
import uk.ramp.metadata.MetadataItem;

public class HashMetadataAppender {
  private final Hasher hasher;

  public HashMetadataAppender(Hasher hasher) {
    this.hasher = hasher;
  }

  public MetadataItem addHash(MetadataItem queryMetaDataItem, boolean shouldVerifyHash) {

    if (queryMetaDataItem.verifiedHash().isEmpty()) {
      return queryMetaDataItem;
    }

    if (queryMetaDataItem.filename().isEmpty()) {
      throw new IllegalStateException("Filename not specified. " + queryMetaDataItem);
    }

    var verifiedHash = queryMetaDataItem.verifiedHash().get();

    String calculatedHash = hasher.hash(queryMetaDataItem.filename().get());

    if (shouldVerifyHash && !calculatedHash.equals(verifiedHash)) {
      throw new IllegalStateException(
          String.format(
              "Verified hash %s does not match calculated hash %s", verifiedHash, calculatedHash));
    }

    return ImmutableMetadataItem.copyOf(queryMetaDataItem).withCalculatedHash(calculatedHash);
  }
}
