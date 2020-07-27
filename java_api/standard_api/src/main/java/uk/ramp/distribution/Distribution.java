package uk.ramp.distribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.EmpiricalDistribution;
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
    uniform(),
    empirical(),
    categorical();
  }

  @JsonProperty("distribution")
  DistributionType internalType();

  @JsonProperty("shape")
  OptionalDouble internalShape();

  @JsonProperty("scale")
  OptionalDouble internalScale();

  @JsonProperty("loc")
  OptionalDouble internalLoc();

  @JsonIgnore
  Optional<List<Number>> empiricalSamples();

  Optional<List<Integer>> bins();

  Optional<List<Number>> weights();

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

  @JsonIgnore
  default Number getSample() {
    return drawSample();
  }

  @Override
  @JsonIgnore
  default List<Number> getSamples() {
    throw new UnsupportedOperationException("Cannot produce list of all samples from distribution");
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
    } else if (internalType().equals(DistributionType.empirical)) {
      var dist = new EmpiricalDistribution();
      dist.load(
          empiricalSamples().orElseThrow().stream().mapToDouble(Number::doubleValue).toArray());
      return dist;
    } else if (internalType().equals(DistributionType.categorical)) {
      List<Integer> bins = bins().orElseThrow();
      List<Double> weights =
          weights().orElseThrow().stream()
              .mapToDouble(Number::doubleValue)
              .boxed()
              .collect(Collectors.toList());
      double[] outcomes =
          IntStream.rangeClosed(bins.get(0), bins.get(bins.size() - 1))
              .mapToDouble(v -> v)
              .toArray();
      List<Double> probabilitiesList =
          IntStream.range(0, bins.size() - 1)
              .mapToObj(
                  idx ->
                      IntStream.range(bins.get(idx), bins.get(idx + 1))
                          .mapToDouble(i -> weights.get(idx))
                          .boxed()
                          .collect(Collectors.toList()))
              .flatMapToDouble(vals -> vals.stream().mapToDouble(Double::doubleValue))
              .boxed()
              .collect(Collectors.toList());

      probabilitiesList.add(weights.get(weights.size() - 1));
      double[] probabilities = probabilitiesList.stream().mapToDouble(i -> i).toArray();

      return new EnumeratedRealDistribution(outcomes, probabilities);
    }
    throw new UnsupportedOperationException(
        String.format("Distribution type %s is not supported.", internalType()));
  }
}
