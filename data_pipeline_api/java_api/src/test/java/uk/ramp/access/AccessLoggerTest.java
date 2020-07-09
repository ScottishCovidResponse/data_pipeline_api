package uk.ramp.access;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import uk.ramp.config.Config;
import uk.ramp.metadata.MetadataItem;

public class AccessLoggerTest {
  private List<AccessEntry> entries;
  private Clock clock;
  private AccessLogWriter accessLogWriter;
  private Config config;
  private Instant openTimestamp;
  private MetadataItem callMetadata;
  private MetadataItem accessMetadata;
  private AccessEntry readEntry;
  private AccessEntry writeEntry;

  @Before
  public void setUp() {
    entries = new ArrayList<>();
    clock = Clock.fixed(Instant.ofEpochMilli(123), ZoneId.systemDefault());
    accessLogWriter = mock(AccessLogWriter.class);
    config = mock(Config.class);
    openTimestamp = Instant.ofEpochMilli(120);
    callMetadata = mock(MetadataItem.class);
    accessMetadata = mock(MetadataItem.class);
    readEntry =
        ImmutableAccessEntry.builder()
            .callMetadata(callMetadata)
            .accessMetadata(accessMetadata)
            .timestamp(Instant.ofEpochMilli(123))
            .type("read")
            .build();
    writeEntry =
        ImmutableAccessEntry.builder()
            .callMetadata(callMetadata)
            .accessMetadata(accessMetadata)
            .timestamp(Instant.ofEpochMilli(123))
            .type("write")
            .build();

    when(config.runId()).thenReturn(Optional.of("run id"));
    when(config.dataDirectory()).thenReturn(Optional.of("data directory"));
  }

  @Test
  public void testLogRead() {
    var accessLogger = new AccessLoggerImpl(entries, clock, accessLogWriter, config, openTimestamp);
    accessLogger.logRead(callMetadata, accessMetadata);
    assertThat(entries).containsExactly(readEntry);
  }

  @Test
  public void testLogWrite() {
    var accessLogger = new AccessLoggerImpl(entries, clock, accessLogWriter, config, openTimestamp);
    accessLogger.logWrite(callMetadata, accessMetadata);
    assertThat(entries).containsExactly(writeEntry);
  }

  @Test
  public void testWriteAccessEntries() {
    entries = List.of(readEntry, writeEntry);
    var accessLogger = new AccessLoggerImpl(entries, clock, accessLogWriter, config, openTimestamp);

    accessLogger.writeAccessEntries();

    var expectedLogData =
        ImmutableAccessLog.builder()
            .accessEntries(entries)
            .dataDirectory("data directory")
            .openTimestamp(Instant.ofEpochMilli(120))
            .closeTimestamp(Instant.ofEpochMilli(123))
            .config(config)
            .runId("run id")
            .build();

    verify(accessLogWriter, times(1)).write(expectedLogData);
  }
}
