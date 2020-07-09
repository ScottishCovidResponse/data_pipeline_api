package uk.ramp.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MatchingMetadataSelectorTest {
  private List<MetadataItem> metadataItems;

  private final MetadataItem item1 =
      ImmutableMetadataItem.builder()
          .dataProduct("test")
          .filename("testFileName0")
          .version("1.0.0")
          .build();

  private final MetadataItem item2 =
      ImmutableMetadataItem.builder()
          .dataProduct("test")
          .filename("testFileName1")
          .version("2.0.0")
          .build();

  @Before
  public void setUp() {
    metadataItems = List.of();
  }

  @Test
  public void simpleReadTest() {
    metadataItems = List.of(item1);
    var matchingMetadataSelector = new MatchingMetadataSelector(metadataItems);

    var query = ImmutableMetadataItem.builder().dataProduct("test").build();
    assertThat(matchingMetadataSelector.find(query)).isEqualTo(item1);
  }

  @Test
  public void testReadLatestVersion() {
    metadataItems = List.of(item1, item2);
    var matchingMetadataSelector = new MatchingMetadataSelector(metadataItems);

    var query = ImmutableMetadataItem.builder().dataProduct("test").build();
    assertThat(matchingMetadataSelector.find(query)).isEqualTo(item2);
  }

  @Test
  public void testReadSpecificVersion() {
    metadataItems = List.of(item1, item2);
    var matchingMetadataSelector = new MatchingMetadataSelector(metadataItems);

    var query = ImmutableMetadataItem.builder().dataProduct("test").version("1.0.0").build();
    assertThat(matchingMetadataSelector.find(query)).isEqualTo(item1);
  }
}
