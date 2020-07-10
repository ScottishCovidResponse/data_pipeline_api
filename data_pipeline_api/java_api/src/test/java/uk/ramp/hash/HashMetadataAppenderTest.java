package uk.ramp.hash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import uk.ramp.metadata.ImmutableMetadataItem;

public class HashMetadataAppenderTest {
  private Hasher hasher;

  @Before
  public void setUp() {
    this.hasher = mock(Hasher.class);
  }

  @Test
  public void testOpenForRead() {
    var query = ImmutableMetadataItem.builder().filename("file1").verifiedHash("hash1").build();

    when(hasher.hash("file1")).thenReturn("hash1");

    var hashVerificationFileApi = new HashMetadataAppender(hasher);

    assertThat(hashVerificationFileApi.addHash(query, true))
        .isEqualTo(query.withCalculatedHash("hash1"));
  }
}
