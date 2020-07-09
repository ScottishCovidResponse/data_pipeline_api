package uk.ramp.hash;

import java.time.Instant;
import uk.ramp.file.FileReader;

public class HasherFactory {
  public Hasher contentsHasher() {
    return new Sha1Hasher();
  }

  public Hasher fileHasher(FileReader fileReader) {
    return fileName -> new Sha1Hasher().hash(fileReader.read(fileName));
  }

  public Hasher fileHasher(FileReader fileReader, Instant openTimestamp) {
    return fileName -> new Sha1Hasher().hash(fileReader.read(fileName) + openTimestamp.toString());
  }
}
