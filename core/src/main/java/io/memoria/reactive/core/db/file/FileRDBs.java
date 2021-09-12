package io.memoria.reactive.core.db.file;

import java.nio.file.Path;

public class FileRDBs {
  public static final String JSON_FILE_EXT = ".json";

  public static long toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(JSON_FILE_EXT, "");
    return Long.parseLong(idxStr);
  }

  public static Path toPath(Path path, long index) {
    var fileName = String.format("%019d%s", index, JSON_FILE_EXT);
    return path.resolve(fileName);
  }

  private FileRDBs() {}

}
