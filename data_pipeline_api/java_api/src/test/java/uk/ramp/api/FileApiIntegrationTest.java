package uk.ramp.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import uk.ramp.hash.Hasher;
import uk.ramp.metadata.ImmutableMetadataItem;

public class FileApiIntegrationTest {
  private String parentPath;

  @Before
  public void setUp() throws IOException, URISyntaxException {
    parentPath = Paths.get(getClass().getResource("/config.yaml").toURI()).getParent().toString();
    Files.deleteIfExists(Path.of(parentPath, "exampleWrite.toml"));
    Files.deleteIfExists(Path.of(parentPath, "access-runId.yaml"));
  }

  @Test
  public void testOpenForRead() throws Exception {
    var query = ImmutableMetadataItem.builder().component("example-estimate").build();
    var buffer = ByteBuffer.allocate(16);
    FileApi fileApi = new FileApi(Path.of(parentPath));
    fileApi.openForRead(query).read(buffer);
    assertThat(new String(buffer.array()).contains("title = \"TOML Example\"\n"));
  }

  @Test
  public void testOpenForWrite() throws IOException {
    var query = ImmutableMetadataItem.builder().filename("exampleWrite.toml").build();
    var buffer = ByteBuffer.allocate(16);
    buffer.put("testWrite".getBytes());
    buffer.flip();
    FileApi fileApi = new FileApi(Path.of(parentPath));
    fileApi.openForWrite(query).write(buffer);
    assertThat(Files.readString(Path.of(parentPath, "exampleWrite.toml"))).isEqualTo("testWrite");
  }

  @Test
  public void testClose() throws IOException {
    FileApi api = new FileApi(Path.of(parentPath));
    api.close();
    assertThat(Files.readString(Path.of(parentPath, "access-runId.yaml")))
        .contains("open_timestamp")
        .contains("close_timestamp")
        .contains("run_id")
        .contains("io");
  }

  @Test
  public void testWriteNewHash() throws IOException {
    var writeFilePath = Path.of(parentPath, "exampleWrite.toml").toString();
    var query = ImmutableMetadataItem.builder().filename("exampleWrite.toml").build();
    var buffer = ByteBuffer.allocate(16).put("testWrite".getBytes()).flip();
    FileApi fileApi = new FileApi(Path.of(parentPath));
    var fileHandle = fileApi.openForWrite(query);
    fileHandle.write(buffer);
    fileHandle.close();
    fileApi.close();

    var calculatedHash = new Hasher().fileHash(writeFilePath);

    assertThat(Files.readString(Path.of(parentPath, "access-runId.yaml")))
        .contains(String.format("calculatedHash: \"%s\"", calculatedHash));
  }

  @Test
  public void testNoIOIfFileHandleNotClosed() throws IOException {
    var query = ImmutableMetadataItem.builder().filename("exampleWrite.toml").build();
    var buffer = ByteBuffer.allocate(16).put("testWrite".getBytes()).flip();
    FileApi fileApi = new FileApi(Path.of(parentPath));
    var fileHandle = fileApi.openForWrite(query);
    fileHandle.write(buffer);
    fileApi.close();

    assertThat(Files.readString(Path.of(parentPath, "access-runId.yaml"))).contains("io: []");
  }
}
