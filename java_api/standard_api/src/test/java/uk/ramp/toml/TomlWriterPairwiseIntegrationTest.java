package uk.ramp.toml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.math3.random.RandomGenerator;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import uk.ramp.distribution.Distribution.DistributionType;
import uk.ramp.distribution.ImmutableDistribution;
import uk.ramp.estimate.ImmutableEstimate;
import uk.ramp.parameters.Components;
import uk.ramp.parameters.ImmutableComponents;
import uk.ramp.samples.ImmutableSamples;

public class TomlWriterPairwiseIntegrationTest {
  private final String expectedToml =
      "[example-estimate]\n"
          + "type = \"point-estimate\"\n"
          + "value = 1.0\n"
          + "\n"
          + "[example-distribution]\n"
          + "type = \"distribution\"\n"
          + "distribution = \"gamma\"\n"
          + "shape = 1\n"
          + "scale = 2\n"
          + "\n"
          + "[example-samples]\n"
          + "type = \"samples\"\n"
          + "samples = [ 1, 2, 3,]";

  private TOMLMapper tomlMapper;
  private RandomGenerator rng;

  @Before
  public void setUp() throws Exception {
    rng = mock(RandomGenerator.class);
    when(rng.nextDouble()).thenReturn(0D);
    tomlMapper = new TOMLMapper(rng);
  }

  @Test
  public void write() throws IOException, JSONException {
    var estimate = ImmutableEstimate.builder().internalValue(1.0).rng(rng).build();
    var distribution =
        ImmutableDistribution.builder()
            .internalType(DistributionType.gamma)
            .internalShape(1)
            .internalScale(2)
            .rng(rng)
            .build();
    var samples = ImmutableSamples.builder().addSamples(1, 2, 3).rng(rng).build();

    Components components =
        ImmutableComponents.builder()
            .putComponents("example-estimate", estimate)
            .putComponents("example-distribution", distribution)
            .putComponents("example-samples", samples)
            .build();

    var writer = new StringWriter();
    var tomlWriter = new TomlWriter(tomlMapper);

    tomlWriter.write(writer, components);

    var actualToml = writer.toString();
    assertThat(actualToml).isNotBlank();
    assertEquals(actualToml, expectedToml, true);
  }
}
