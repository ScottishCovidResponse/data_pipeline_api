package uk.ramp.distribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.OptionalDouble;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.immutables.value.Value.Immutable;
import uk.ramp.parameters.Component;

@JsonSerialize
@Immutable
@JsonDeserialize
@JsonInclude(Include.NON_EMPTY)
public interface Distribution extends Component {
  enum DistributionType {
    gamma(),
    exponential(),
    uniform()
  }

  @JsonProperty("distribution")
  DistributionType internalType();

  @JsonProperty("shape")
  OptionalDouble internalShape();

  @JsonProperty("scale")
  OptionalDouble internalScale();

  @JsonProperty("loc")
  OptionalDouble internalLoc();

  private double mean() {
    return underlyingDistribution().getNumericalMean();
  }

  private Number drawSample() {
    return underlyingDistribution().sample();
  }

  @Override
  @JsonIgnore
  default Number getEstimate() {
    return mean();
  }

  @Override
  @JsonIgnore
  default Number getSample() {
    return drawSample();
  }

  @Override
  @JsonIgnore
  default Distribution getDistribution() {
    return this;
  }

  private RealDistribution underlyingDistribution() {
    if (internalType().equals(DistributionType.gamma)) {
      return new GammaDistribution(internalShape().orElseThrow(), internalScale().orElseThrow());
    } else if (internalType().equals(DistributionType.exponential)) {
      return new ExponentialDistribution(internalScale().orElseThrow());
    } else if (internalType().equals(DistributionType.uniform)) {
      return new UniformRealDistribution();
    }
    throw new UnsupportedOperationException(
        String.format("Distribution type %s is not supported.", internalType()));
  }
}
