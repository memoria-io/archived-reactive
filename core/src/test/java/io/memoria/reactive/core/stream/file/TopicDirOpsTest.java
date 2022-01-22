package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.FileOps;
import io.vavr.collection.List;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;

class TopicDirOpsTest {
  private static final Path TEST_DIR = Path.of("/tmp/dirWatcherTest");
  private static final int NEW_FILES_COUNT = 10;

  @BeforeEach
  void beforeEach() {
    FileOps.deleteDir(TEST_DIR).subscribe();
    FileOps.createDir(TEST_DIR).subscribe();
    TopicDirOps.createIndex(TEST_DIR).subscribe();
  }

  @Test
  void stream() {
    // Given
    int existingFilesCount = 100;
    createSomeFiles(TEST_DIR, 0, existingFilesCount).subscribe();
    var fileList = new ArrayList<Path>();
    // when
    new Thread(() -> TopicDirOps.stream(TEST_DIR)
                                .take(NEW_FILES_COUNT + existingFilesCount)
                                .subscribe(fileList::add)).start();
    createSomeFiles(TEST_DIR, existingFilesCount, NEW_FILES_COUNT).delaySubscription(Duration.ofMillis(200))
                                                                  .subscribe();
    // Then
    Awaitility.await()
              .atMost(Duration.ofSeconds(2))
              .until(() -> fileList.size() == NEW_FILES_COUNT + existingFilesCount);
  }

  @Test
  void watch() {
    // Given
    var fileList = new ArrayList<Path>();
    createSomeFiles(TEST_DIR, 0, NEW_FILES_COUNT).delaySubscription(Duration.ofMillis(200)).subscribe();

    // when
    new Thread(() -> TopicDirOps.watch(TEST_DIR).take(NEW_FILES_COUNT).subscribe(fileList::add)).start();

    // Then
    Awaitility.await().atMost(Duration.ofSeconds(2)).until(() -> fileList.size() == NEW_FILES_COUNT);
    var expected = List.range(0, NEW_FILES_COUNT).map(TopicDirOps::toFileName).map(TEST_DIR::resolve);
    Assertions.assertEquals(expected, List.ofAll(fileList));
  }

  private static Flux<Path> createSomeFiles(Path dir, int start, int count) {
    return Flux.range(start, count).concatMap(i -> TopicDirOps.write(dir, i, "hello world"));
  }
}

