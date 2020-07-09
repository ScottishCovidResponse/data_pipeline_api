package uk.ramp.metadata;

import uk.ramp.access.AccessLogger;

public class AccessLoggedMetadataSelector implements MetadataSelector {
  private final MetadataSelector metadataSelector;
  private final AccessLogger accessLogger;

  AccessLoggedMetadataSelector(MetadataSelector metadataSelector, AccessLogger accessLogger) {
    this.metadataSelector = metadataSelector;
    this.accessLogger = accessLogger;
  }

  @Override
  public MetadataItem find(MetadataItem queryMetadataItem) {
    var readMetaDataItem = metadataSelector.find(queryMetadataItem);
    accessLogger.logRead(queryMetadataItem, readMetaDataItem);
    return readMetaDataItem;
  }
}
