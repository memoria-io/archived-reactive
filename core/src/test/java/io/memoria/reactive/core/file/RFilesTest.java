package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class RFilesTest {
  private static final Path EMPTY_DIR = Path.of("/tmp/emptyDir");
  private static final Path SOME_FILE_PATH = EMPTY_DIR.resolve("file.txt");

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(EMPTY_DIR).subscribe();
    RFiles.clean(EMPTY_DIR).subscribe();
  }

  @Test
  @DisplayName("Should append or create a file")
  void create() throws IOException {
    // When
    StepVerifier.create(RFiles.write(SOME_FILE_PATH, "hello world")).expectNext(SOME_FILE_PATH).verifyComplete();
    // Then
    var str = new String(Files.readAllBytes(SOME_FILE_PATH));
    Assertions.assertEquals("hello world", str);
  }

  @Test
  void delete() throws IOException {
    // Given
    Files.createFile(SOME_FILE_PATH);
    // When
    var deleteFile = RFiles.delete(SOME_FILE_PATH);
    // Then
    StepVerifier.create(deleteFile).expectNext(SOME_FILE_PATH).verifyComplete();
  }

  @Test
  void deleteAll() {
    // Given
    var files = createSomeFiles(10).block();
    // When
    var deleteFiles = RFiles.delete(files);
    // Then
    assert files != null;
    StepVerifier.create(deleteFiles).expectNextCount(files.length()).verifyComplete();
  }

  @Test
  void lastFile() {
    // Given
    var files = createSomeFiles(10).block();
    // When
    var lastFile = RFiles.lastModified(EMPTY_DIR);
    // Then
    assert files != null;
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void list() {
    // Given
    var listFlux = RFiles.list(EMPTY_DIR);
    // Then
    StepVerifier.create(listFlux).expectNext().verifyComplete();
    StepVerifier.create(listFlux.count()).expectNext(0L).verifyComplete();
  }

  @Test
  void read() throws IOException {
    // Given
    Files.writeString(SOME_FILE_PATH, "welcome");
    // When
    var read = RFiles.read(SOME_FILE_PATH);
    // Then
    StepVerifier.create(read).expectNext("welcome").verifyComplete();
  }

  private Mono<List<Path>> createSomeFiles(int count) {
    return Flux.range(0, count)
               .concatMap(i -> RFiles.write(EMPTY_DIR.resolve(i + ".json"), "hi" + i))
               .collectList()
               .map(List::ofAll);
  }
}
