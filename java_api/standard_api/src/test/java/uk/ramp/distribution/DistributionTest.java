package uk.ramp.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.util.stream.IntStream;
import org.junit.Test;
import uk.ramp.distribution.Distribution.DistributionType;

public class DistributionTest {
  private final Distribution distribution = ImmutableDistribution.builder()
      .internalType(DistributionType.gamma)
        .internalShape(1)
        .internalScale(2)
        .build();

  @Test
  public void derivedEstimateFromDistribution() {
    assertThat(distribution.getEstimate().floatValue()).isEqualTo(2F);
  }

  @Test
  public void derivedSampleFromDistribution() {
    var distributionAvgUsingSamples = IntStream.rangeClosed(0, 1000000)
        .parallel()
        .mapToDouble(i -> distribution.getSample().doubleValue())
        .average();
    assertThat(distributionAvgUsingSamples).hasValueCloseTo(2D, offset(0.01));
  }

  @Test
  public void derivedDistributionFromDistribution() {
    assertThat(distribution.getDistribution()).isEqualTo(distribution);
  }
}