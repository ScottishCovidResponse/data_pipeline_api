package uk.ramp.config;

import static uk.ramp.file.FileDirectoryNormaliser.normalisePath;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import uk.ramp.metadata.ImmutableMetadataItem;

@Immutable
@JsonSerialize
@JsonDeserialize
public interface Config {
  @JsonProperty("data_directory")
  Optional<String> dataDirectory();

  default String normalisedDataDirectory() {
    var dataDirectory = dataDirectory().orElse(".");
    var dataDirectoryPath = Paths.get(dataDirectory);

    if (dataDirectoryPath.isAbsolute()) {
      return dataDirectoryPath.toString();
    }

    return Paths.get(Paths.get(parentPath().orElseThrow()).toString(), dataDirectoryPath.toString())
        .toString();
  }

  @JsonProperty("access_log")
  Optional<String> accessLog();

  @Derived
  default boolean accessLogDisabled() {
    return accessLog().orElse("").equalsIgnoreCase("false");
  }

  Optional<String> parentPath();

  default String normalisedAccessLogPath() {
    var accessLogPath =
        accessLog().orElse("access-{run_id}.yaml").replace("{run_id}", runId().orElseThrow());

    return normalisePath(parentPath().orElseThrow(), accessLogPath);
  }

  @JsonProperty("fail_on_hash_mismatch")
  Optional<Boolean> failOnHashMisMatch();

  @JsonProperty("read")
  List<ImmutableOverrideItem> readQueryOverrides();

  @JsonProperty("write")
  List<ImmutableOverrideItem> writeQueryOverrides();

  @JsonProperty("run_id")
  Optional<String> runId();

  @Immutable
  @JsonSerialize
  @JsonDeserialize
  interface OverrideItem {
    Optional<ImmutableMetadataItem> where();

    Optional<ImmutableMetadataItem> use();
  }
}
