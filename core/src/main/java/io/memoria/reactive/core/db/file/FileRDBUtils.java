package io.memoria.reactive.core.db.file;

import java.nio.file.Path;

import static io.memoria.reactive.core.db.file.FileRDB.FILE_EXT;

class FileRDBUtils {
  private FileRDBUtils() {}

  static long toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(FILE_EXT, "");
    return Long.parseLong(idxStr);
  }

  static Path toPath(Path path, long index) {
    var fileName = String.format("%019d%s", index, FILE_EXT);
    return path.resolve(fileName);
  }
}
