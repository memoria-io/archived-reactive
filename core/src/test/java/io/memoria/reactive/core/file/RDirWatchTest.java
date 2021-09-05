package io.memoria.reactive.core.file;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.core.file.TestUtils.END;
import static io.memoria.reactive.core.file.TestUtils.PATHS_ARR;

class RDirWatchTest {
  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(TestUtils.EMPTY_DIR).subscribe();
    RFiles.clean(TestUtils.EMPTY_DIR).subscribe();
  }

  @Test
  void watch() {
    // Given
    new Thread(() -> Try.of(TestUtils::writeFiles).get()).start();
    // When
    var w = RDirWatch.watch(TestUtils.EMPTY_DIR).take(END);
    // Then
    StepVerifier.create(w).expectNext(PATHS_ARR).verifyComplete();
  }
}
