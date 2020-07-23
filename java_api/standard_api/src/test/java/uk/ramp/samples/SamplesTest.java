package uk.ramp.samples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;
import java.util.function.Predicate;
import org.junit.Ignore;
import org.junit.Test;

public class SamplesTest {

  @Test
  public void derivedEstimateFromSamples() {
    var samples = ImmutableSamples.builder().addSamples(1, 2, 3).build();
    assertThat(samples.getEstimate()).isEqualTo(2);
  }

  @Test
  @Ignore
  // TODO - large numbers are currently unsupported.
  public void derivedEstimateLargeSamples() {
    var largeValue = 100_000_000_000_000_000L;
    var samples = ImmutableSamples.builder()
        .addSamples(largeValue, largeValue + 1, largeValue + 2)
        .build();
    assertThat(samples.getEstimate()).isEqualTo(largeValue + 1);
  }

  @Test
  public void derivedSampleFromSamples() {
    var samples = ImmutableSamples.builder().addSamples(1, 2, 3).build();
    assertThat(samples.getSample().intValue()).isIn(1, 2, 3);
  }

  @Test
  public void derivedDistributionFromSamples() {
    var samples = ImmutableSamples.builder().addSamples(1, 2, 3).build();
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(samples::getDistribution);
  }
}