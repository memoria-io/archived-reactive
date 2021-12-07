package io.memoria.reactive.core.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;

import static io.memoria.reactive.core.file.TestUtils.FILES_COUNT;
import static io.memoria.reactive.core.file.TestUtils.createSomeFilesDelayed;
import static org.awaitility.Awaitility.await;

class DirWatcherTest {
  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(TestUtils.EMPTY_DIR).subscribe();
    RFiles.clean(TestUtils.EMPTY_DIR).subscribe();
  }

  @Test
  void watch() {
    // Given
    var watchedPaths = new ArrayList<Path>();
    // when
    new Thread(() -> DirWatcher.watch(TestUtils.EMPTY_DIR).take(FILES_COUNT).subscribe(watchedPaths::add)).start();
    createSomeFilesDelayed(Duration.ofMillis(200)).block();
    // Then
    await().atMost(Duration.ofSeconds(2)).until(() -> watchedPaths.size() == FILES_COUNT);
  }
}

