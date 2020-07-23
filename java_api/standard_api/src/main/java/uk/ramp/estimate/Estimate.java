package uk.ramp.estimate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import uk.ramp.distribution.Distribution;
import uk.ramp.parameters.Component;

@Immutable
@JsonDeserialize
@JsonSerialize
public interface Estimate extends Component {
  @JsonProperty("value")
  Number internalValue();

  @Override
  @JsonIgnore
  default Number getEstimate() {
    return internalValue();
  }

  @Override
  @JsonIgnore
  default Number getSample() {
    return internalValue();
  }

  @Override
  @JsonIgnore
  default Distribution getDistribution() {
      throw new UnsupportedOperationException("Cannot produce an distribution from an estimate parameter");
  }
}
