package uk.ramp.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MetadataItemTest {

  @Parameterized.Parameter(0)
  public String v1;

  @Parameterized.Parameter(1)
  public String v2;

  @Parameterized.Parameters(name = "{index}: Test with v1={0}, v2 ={1}")
  public static Collection<Object[]> data() {
    String[][] data =
        new String[][] {
          {"1.0.0", "2.0.0"},
          {"0.1.0", "0.2.0"},
          {"0.0.1", "0.0.2"},
          {"1.0.0", "11.0.0"},
          {"1.9", "1.10"},
          {"0.0.1", "1"},
          {"0", "0.1"},
          {"0", "0.0.1"}
        };
    return Arrays.asList(data);
  }

  @Test
  public void testVersionComparison() {
    var meta1 = ImmutableMetadataItem.builder().internalVersion(v1).build();
    var meta2 = ImmutableMetadataItem.builder().internalVersion(v2).build();

    assertThat(meta1.version()).isLessThan(meta2.version());
    assertThat(meta1.version().toString()).isEqualTo(v1);
    assertThat(meta2.version().toString()).isEqualTo(v2);
  }
}
