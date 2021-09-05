package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;

import static io.memoria.reactive.core.file.TestUtils.FILES;
import static io.memoria.reactive.core.file.TestUtils.PATHS;
import static io.memoria.reactive.core.file.TestUtils.PATHS_ARR;

class RFilesTest {

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(TestUtils.EMPTY_DIR).subscribe();
    RFiles.clean(TestUtils.EMPTY_DIR).subscribe();
  }

  @Test
  @DisplayName("Should append or create a file")
  void create() throws IOException {
    // When
    var writeFileMono = RFiles.write(new RFile(TestUtils.EMPTY_DIR_FILE, "hello world"));
    StepVerifier.create(writeFileMono).expectNext(TestUtils.EMPTY_DIR_FILE).verifyComplete();
    // Then
    var str = new String(Files.readAllBytes(TestUtils.EMPTY_DIR_FILE));
    Assertions.assertEquals(str, "hello world");
  }

  @Test
  void delete() throws IOException {
    // Given
    Files.createFile(TestUtils.EMPTY_DIR_FILE);
    // When
    var deleteFile = RFiles.delete(TestUtils.EMPTY_DIR_FILE);
    // Then
    StepVerifier.create(deleteFile).expectNext(TestUtils.EMPTY_DIR_FILE).verifyComplete();
  }

  @Test
  void deleteAll() {
    // Given
    var files = TestUtils.writeFiles();
    // When
    var deleteFiles = RFiles.delete(files);
    // Then
    StepVerifier.create(deleteFiles).expectNextCount(files.length()).verifyComplete();
  }

  @Test
  void lastFile() {
    // Given
    var files = TestUtils.writeFiles();
    // When
    var lastFile = RFiles.lastModified(TestUtils.EMPTY_DIR);
    // Then
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void publish() {
    // Given
    var files = Flux.fromIterable(FILES);
    // When
    var pub = RFiles.publish(files);
    // Then
    StepVerifier.create(pub).expectNext(PATHS_ARR).verifyComplete();
  }

  @Test
  void read() throws IOException {
    // Given
    Files.writeString(TestUtils.EMPTY_DIR_FILE, "welcome");
    // When
    var read = RFiles.read(TestUtils.EMPTY_DIR_FILE);
    // Then
    StepVerifier.create(read).expectNext("welcome").verifyComplete();
  }

  @Test
  void readDirectory() {
    // Given
    TestUtils.writeFiles();
    // When
    var readDir = RFiles.readDir(TestUtils.EMPTY_DIR)
                        .flatMapMany(Flux::fromIterable)
                        .map(RFile::path)
                        .collectList()
                        .map(List::ofAll)
                        .map(List::toSet);
    // Then
    StepVerifier.create(readDir).expectNext(PATHS.toSet()).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    new Thread(TestUtils::writeFiles).start();
    // When
    var sub = RFiles.subscribe(TestUtils.EMPTY_DIR).map(RFile::path).take(TestUtils.END);
    // Then
    StepVerifier.create(sub).expectNext(PATHS_ARR).verifyComplete();
  }

  @Test
  void writeMany() throws IOException {
    // When
    var writeAll = RFiles.write(FILES);
    // Then
    StepVerifier.create(writeAll).expectNextCount(1).verifyComplete();
    // And
    var expectedPaths = List.ofAll(Files.list(TestUtils.EMPTY_DIR).sorted().toList());
    Assertions.assertEquals(expectedPaths, PATHS);
  }
}
