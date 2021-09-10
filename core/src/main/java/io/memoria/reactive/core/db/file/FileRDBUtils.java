package io.memoria.reactive.core.db.file;

import io.memoria.reactive.core.file.RFiles;
import reactor.core.publisher.Flux;

import java.nio.file.Path;

class FileRDBUtils {
  private FileRDBUtils() {}

  static Flux<Long> sortedList(Path path) {
    return RFiles.list(path).flatMapMany(Flux::fromIterable).map(FileRDBUtils::toIndex).sort();
  }

  static long toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(FileRDB.FILE_EXT, "");
    return Long.parseLong(idxStr);
  }

  static Path toPath(Path path, long index) {
    return path.resolve(index + FileRDB.FILE_EXT);
  }
}
