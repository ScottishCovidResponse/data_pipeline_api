package uk.ramp.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import uk.ramp.hash.Hasher;

public class HashVerificationMetadataSelectorTest {
  private MetadataSelector metadataSelector;
  private Hasher hasher;

  @Before
  public void setUp() {
    this.metadataSelector = mock(MetadataSelector.class);
    this.hasher = mock(Hasher.class);
  }

  @Test
  public void testOpenForRead() {
    var query = ImmutableMetadataItem.builder().filename("file1").verifiedHash("hash1").build();

    when(metadataSelector.find(query)).thenReturn(query);
    when(hasher.hash("file1")).thenReturn("hash1");

    var hashVerificationFileApi =
        new HashVerificationMetadataSelector(metadataSelector, hasher, true);

    assertThat(hashVerificationFileApi.find(query)).isEqualTo(query.withCalculatedHash("hash1"));
  }
}
