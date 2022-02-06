package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.FileOps;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;

class TopicDirOpsTest {
  private static final Path TEST_DIR = Path.of("/tmp/dirWatcherTest");
  private static final int EXISTING_FILES_COUNT = 100;
  private static final int NEW_FILES_COUNT = 1000;

  @BeforeEach
  void beforeEach() {
    FileOps.deleteDir(TEST_DIR).subscribe();
    FileOps.createDir(TEST_DIR).subscribe();
    TopicDirOps.createIndex(TEST_DIR).subscribe();
  }

  @Test
  void stream() {
    // Given
    createSomeFiles(0, EXISTING_FILES_COUNT).subscribe();
    // when
    var streamFlux = TopicDirOps.stream(TEST_DIR).limitRate(1).take(NEW_FILES_COUNT + EXISTING_FILES_COUNT);
    createSomeFiles(EXISTING_FILES_COUNT, NEW_FILES_COUNT).delaySubscription(Duration.ofMillis(200)).subscribe();
    // Then
    StepVerifier.create(streamFlux).expectNextCount(NEW_FILES_COUNT + EXISTING_FILES_COUNT).verifyComplete();
  }

  @Test
  void watch() {
    // Given
    createSomeFiles(0, NEW_FILES_COUNT).delaySubscription(Duration.ofMillis(200)).subscribe();
    // when
    var watchFlux = TopicDirOps.watch(TEST_DIR).take(NEW_FILES_COUNT);
    // Then
    var expected = List.range(0, NEW_FILES_COUNT).map(TopicDirOps::toFileName).map(TEST_DIR::resolve);
    StepVerifier.create(watchFlux).expectNextSequence(expected).verifyComplete();
  }

  private static Flux<Path> createSomeFiles(int start, int count) {
    return Flux.range(start, count).concatMap(i -> TopicDirOps.write(TEST_DIR, i, "hello world"));
  }
}

