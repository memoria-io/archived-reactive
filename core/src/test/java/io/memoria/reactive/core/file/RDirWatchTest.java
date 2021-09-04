package io.memoria.reactive.core.file;

import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.core.file.Utils.END;
import static io.memoria.reactive.core.file.Utils.FILES_ARR;

class RDirWatchTest {
  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(Utils.EMPTY_DIR).subscribe();
    RFiles.clean(Utils.EMPTY_DIR).subscribe();
  }

  @Test
  void watch() {
    // Given
    new Thread(() -> Try.of(Utils::writeFiles).get()).start();
    // When
    var w = RDirWatch.watch(Utils.EMPTY_DIR).take(END);
    // Then
    StepVerifier.create(w).expectNext(FILES_ARR).verifyComplete();
  }
}
