package uk.ramp.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.immutables.value.Value.Immutable;
import uk.ramp.config.Config.OverrideItem;

@Immutable
@JsonSerialize
@JsonDeserialize
public interface MetadataItem {
  Optional<String> filename();

  Optional<String> version();

  Optional<String> extension();

  Optional<String> component();

  @JsonProperty("data_product")
  Optional<String> dataProduct();

  @JsonProperty("run_id")
  Optional<String> runId();

  @JsonProperty("verified_hash")
  Optional<String> verifiedHash();

  Optional<String> calculatedHash();

  Optional<String> source();

  default boolean isSuperSetOf(MetadataItem key) {
    return key.filename().map(k -> k.equals(filename().orElse(""))).orElse(true)
        && key.component().map(k -> k.equals(component().orElse(""))).orElse(true)
        && key.dataProduct().map(k -> k.equals(dataProduct().orElse(""))).orElse(true)
        && key.version().map(k -> k.equals(version().orElse(""))).orElse(true)
        && key.extension().map(k -> k.equals(extension().orElse(""))).orElse(true)
        && key.verifiedHash().map(k -> k.equals(verifiedHash().orElse(""))).orElse(true)
        && key.calculatedHash().map(k -> k.equals(calculatedHash().orElse(""))).orElse(true)
        && key.runId().map(k -> k.equals(runId().orElse(""))).orElse(true)
        && key.source().map(k -> k.equals(source().orElse(""))).orElse(true);
  }

  default MetadataItem applyOverrides(List<OverrideItem> readOverrides) {
    List<MetadataItem> overridesToApply =
        readOverrides.stream()
            .filter(
                readOverrideItem ->
                    this.isSuperSetOf(
                        readOverrideItem.where().orElse(ImmutableMetadataItem.copyOf(this))))
            .map(OverrideItem::use)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

    MetadataItem overridenMetadataItem = ImmutableMetadataItem.copyOf(this);
    for (MetadataItem override : overridesToApply) {
      overridenMetadataItem = applyOverride(overridenMetadataItem, override);
    }
    return overridenMetadataItem;
  }

  private MetadataItem applyOverride(MetadataItem baseMetadata, MetadataItem metadataOverride) {
    var newMetadataItem = ImmutableMetadataItem.copyOf(baseMetadata);

    if (metadataOverride.filename().isPresent()) {
      newMetadataItem = newMetadataItem.withFilename(metadataOverride.filename().get());
    }

    if (metadataOverride.component().isPresent()) {
      newMetadataItem = newMetadataItem.withComponent(metadataOverride.component().get());
    }

    if (metadataOverride.dataProduct().isPresent()) {
      newMetadataItem = newMetadataItem.withDataProduct(metadataOverride.dataProduct().get());
    }

    if (metadataOverride.version().isPresent()) {
      newMetadataItem = newMetadataItem.withVersion(metadataOverride.version().get());
    }

    if (metadataOverride.extension().isPresent()) {
      newMetadataItem = newMetadataItem.withExtension(metadataOverride.extension().get());
    }

    if (metadataOverride.verifiedHash().isPresent()) {
      newMetadataItem = newMetadataItem.withVerifiedHash(metadataOverride.verifiedHash().get());
    }

    if (metadataOverride.calculatedHash().isPresent()) {
      newMetadataItem = newMetadataItem.withCalculatedHash(metadataOverride.calculatedHash().get());
    }

    if (metadataOverride.runId().isPresent()) {
      newMetadataItem = newMetadataItem.withRunId(metadataOverride.runId().get());
    }

    if (metadataOverride.source().isPresent()) {
      newMetadataItem = newMetadataItem.withSource(metadataOverride.source().get());
    }

    return newMetadataItem;
  }
}
