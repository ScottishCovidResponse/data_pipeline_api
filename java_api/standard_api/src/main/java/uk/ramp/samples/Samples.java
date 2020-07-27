package uk.ramp.samples;

import static uk.ramp.samples.Sampler.sampleFrom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.apache.commons.math3.distribution.RealDistribution;
import org.immutables.value.Value.Immutable;
import uk.ramp.distribution.Distribution;
import uk.ramp.parameters.Component;

@Immutable
@JsonSerialize
@JsonDeserialize
public interface Samples extends Component {
  List<Number> samples();

  @JsonIgnore
  default Number mean() {
    double mean = samples().stream()
        .mapToDouble(Number::doubleValue)
        .average()
        .orElseThrow();

    if (mean % 1 == 0) {
      return (int) mean;
    }

    return mean;
  }

  @Override
  @JsonIgnore
  default Number getEstimate() {
    return mean();
  }

  @Override
  @JsonIgnore
  default Number getSample() {
    return sampleFrom(samples());
  }

  @Override
  @JsonIgnore
  default Distribution getDistribution() {
    throw new UnsupportedOperationException("Cannot produce a distribution from a samples parameter");
  }
}
