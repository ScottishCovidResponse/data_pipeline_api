package uk.ramp.file;

import java.nio.file.Path;

public class FileDirectoryNormaliser {
  private final String parentPath;

  public FileDirectoryNormaliser(String parentPath) {
    this.parentPath = parentPath;
  }

  public String normalisePath(String path) {
    if (Path.of(path).isAbsolute()) {
      return path;
    }

    return Path.of(parentPath, path).toString();
  }

  public static String normalisePath(String parentPath, String path) {
    return new FileDirectoryNormaliser(parentPath).normalisePath(path);
  }

  public String parentPath() {
    return parentPath;
  }
}
