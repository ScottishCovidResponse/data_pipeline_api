package uk.ramp.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import uk.ramp.access.AccessLogger;

public class AccessLoggedMetadataSelectorTest {
  private MetadataSelector baseMetadataSelector;
  private AccessLogger accessLogger;

  @Before
  public void setUp() {
    baseMetadataSelector = mock(MetadataSelector.class);
    accessLogger = mock(AccessLogger.class);
  }

  @Test
  public void testOpenForRead() {
    var query = mock(MetadataItem.class);
    var result = mock(MetadataItem.class);

    when(baseMetadataSelector.find(query)).thenReturn(result);
    var accessLoggedFileApi = new AccessLoggedMetadataSelector(baseMetadataSelector, accessLogger);

    assertThat(accessLoggedFileApi.find(query)).isEqualTo(result);
    verify(accessLogger, times(1)).logRead(query, result);
  }
}
