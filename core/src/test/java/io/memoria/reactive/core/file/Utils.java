package io.memoria.reactive.core.file;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Utils {
  static final int START = 0;
  static final int END = 3;
  static final Path EMPTY_DIR = Path.of("/tmp/emptyDir");
  static final Path SOME_FILE = EMPTY_DIR.resolve("file.txt");
  static final List<Tuple2<Path, String>> FILES_TUPLE = files();
  static final List<Path> FILES_LIST = FILES_TUPLE.map(Tuple2::_1);
  static final Path[] FILES_ARR = FILES_LIST.toJavaArray(Path[]::new);

  private Utils() {}

  private static List<Tuple2<Path, String>> files() {
    return List.range(START, END)
               .map(i -> Tuple.of(EMPTY_DIR.resolve("file%d.txt".formatted(i)), "hello world%d".formatted(i)));
  }

  static List<Path> writeFiles() throws IOException, InterruptedException {
    var files = files();
    for (Tuple2<Path, String> tup : files) {
      Thread.sleep(100);
      Files.writeString(tup._1, tup._2);
    }
    return files.map(Tuple2::_1);
  }
}
