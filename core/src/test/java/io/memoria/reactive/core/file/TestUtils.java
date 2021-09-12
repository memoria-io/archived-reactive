package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

class TestUtils {
  static final int FILES_COUNT = 3;
  static final Path EMPTY_DIR = Path.of("/tmp/emptyDir");
  static final Path SOME_FILE_PATH = EMPTY_DIR.resolve("file.txt");
  static final List<RFile> FILES = files();
  static final RFile[] FILES_ARR = FILES.toJavaArray(RFile[]::new);

  private TestUtils() {}

  static List<Path> createSomeFiles() {
    var files = files();
    try {
      for (RFile f : files) {
        Files.writeString(f.path(), f.content());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return files.map(RFile::path);
  }

  static Mono<List<Path>> createSomeFilesDelayed(Duration duration) {
    return Mono.fromCallable(TestUtils::createSomeFiles).delaySubscription(duration);
  }

  private static List<RFile> files() {
    return List.range(0, FILES_COUNT)
               .map(i -> new RFile(EMPTY_DIR.resolve("file%d.txt".formatted(i)), "hello world%d".formatted(i)));
  }
}
