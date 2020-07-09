package uk.ramp.api;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import uk.ramp.access.AccessLogger;
import uk.ramp.access.AccessLoggerFactory;
import uk.ramp.config.ConfigFactory;
import uk.ramp.file.FileDirectoryNormaliser;
import uk.ramp.file.FileReader;
import uk.ramp.hash.HasherFactory;
import uk.ramp.metadata.MetadataItem;
import uk.ramp.metadata.MetadataSelector;
import uk.ramp.metadata.MetadataSelectorFactory;
import uk.ramp.overrides.OverridesApplier;
import uk.ramp.yaml.YamlFactory;

/** Java implementation of Data Pipeline File API. */
public class FileApi implements AutoCloseable {
  private final MetadataSelector metadataSelector;
  private final AccessLogger accessLogger;
  private final OverridesApplier overridesApplier;
  private final FileDirectoryNormaliser fileDirectoryNormaliser;

  public FileApi(Path configFolderPath) {
    this(Clock.systemUTC(), configFolderPath);
  }

  FileApi(Clock clock, Path parentPath) {
    var hasherFactory = new HasherFactory();
    var metadataSelectorFactory = new MetadataSelectorFactory();
    var configFactory = new ConfigFactory();
    var accessLoggerFactory = new AccessLoggerFactory();
    var yamlFactory = new YamlFactory();
    var openTimestamp = clock.instant();
    this.fileDirectoryNormaliser = new FileDirectoryNormaliser(parentPath.toString());
    var config =
        configFactory.config(
            yamlFactory.yamlReader(),
            hasherFactory,
            new FileReader(),
            openTimestamp,
            fileDirectoryNormaliser);
    this.overridesApplier = new OverridesApplier(config);
    this.accessLogger =
        accessLoggerFactory.accessLogger(config, yamlFactory.yamlWriter(), clock, openTimestamp);
    this.metadataSelector =
        metadataSelectorFactory.metadataSelector(
            hasherFactory.contentsHasher(),
            yamlFactory.yamlReader(),
            config.failOnHashMisMatch().orElse(true),
            fileDirectoryNormaliser);
  }

  /**
   * Return file for reading corresponding to the given metadata. The file contents are hashed, and
   * a record is made of the read.
   *
   * @return FileChannel for input
   * @param query input query
   */
  public FileChannel openForRead(MetadataItem query) throws IOException {
    var overridenQuery = overridesApplier.applyReadOverrides(query);
    var metaDataItem = metadataSelector.find(overridenQuery);
    var filename = metaDataItem.filename().orElseThrow();
    var normalisedFilename = fileDirectoryNormaliser.normalisePath(filename);
    accessLogger.logRead(query, metaDataItem);
    return FileChannel.open(Path.of(normalisedFilename), READ);
  }

  /**
   * Return matching file for update corresponding to the given metadata. When the file is closed
   * the file contents are hashed, and a record is made of the write.
   *
   * @return FileChannel for output
   * @param query input query
   */
  public FileChannel openForWrite(MetadataItem query) throws IOException {
    var overridenQuery = overridesApplier.applyWriteOverrides(query);
    var filename = overridenQuery.filename().orElseThrow();
    var normalisedFilename = fileDirectoryNormaliser.normalisePath(filename);
    Files.createDirectories(Path.of(fileDirectoryNormaliser.parentPath()));
    Files.createFile(Path.of(normalisedFilename));
    accessLogger.logWrite(query, overridenQuery);
    return FileChannel.open(Path.of(normalisedFilename), WRITE);
  }

  /** Close the session and write the access log. */
  @Override
  public void close() {
    accessLogger.writeAccessEntries();
  }
}
