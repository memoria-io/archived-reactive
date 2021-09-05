package io.memoria.reactive.core.file;

import io.vavr.collection.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class TestUtils {
  static final int START = 0;
  static final int END = 3;
  static final Path EMPTY_DIR = Path.of("/tmp/emptyDir");
  static final Path EMPTY_DIR_FILE = EMPTY_DIR.resolve("file.txt");
  static final List<RFile> FILES = files();
  static final List<Path> PATHS = FILES.map(RFile::path);
  static final Path[] PATHS_ARR = PATHS.toJavaArray(Path[]::new);

  private TestUtils() {}

  static List<Path> writeFiles() throws IOException, InterruptedException {
    var files = files();
    for (RFile f : files) {
      Thread.sleep(100);
      Files.writeString(f.path(), f.content());
    }
    return files.map(RFile::path);
  }

  private static List<RFile> files() {
    return List.range(START, END)
               .map(i -> new RFile(EMPTY_DIR.resolve("file%d.txt".formatted(i)), "hello world%d".formatted(i)));
  }
}
