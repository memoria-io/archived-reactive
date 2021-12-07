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
import static io.memoria.reactive.core.file.TestUtils.SOME_FILE_PATH;

class RFilesTest {

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(TestUtils.EMPTY_DIR).subscribe();
    RFiles.clean(TestUtils.EMPTY_DIR).subscribe();
  }

  @Test
  @DisplayName("Should append or create a file")
  void create() throws IOException {
    // Given
    var helloWorldFile = new RFile(SOME_FILE_PATH, "hello world");
    // When
    StepVerifier.create(RFiles.write(helloWorldFile)).expectNext(helloWorldFile).verifyComplete();
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
    var files = TestUtils.createSomeFiles();
    // When
    var deleteFiles = RFiles.delete(files);
    // Then
    StepVerifier.create(deleteFiles).expectNextCount(files.length()).verifyComplete();
  }

  @Test
  void lastFile() {
    // Given
    var files = TestUtils.createSomeFiles();
    // When
    var lastFile = RFiles.lastModified(TestUtils.EMPTY_DIR);
    // Then
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void list() {
    // Given
    var listFlux = RFiles.list(TestUtils.EMPTY_DIR);
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

  @Test
  void readDirectory() {
    // Given
    var writtenPaths = TestUtils.createSomeFiles().toSet();
    // When
    var readDir = RFiles.readDir(TestUtils.EMPTY_DIR)
                        .flatMapMany(Flux::fromIterable)
                        .map(RFile::path)
                        .collectList()
                        .map(List::ofAll)
                        .map(List::toSet);
    // Then
    StepVerifier.create(readDir).expectNext(writtenPaths).verifyComplete();
  }

  @Test
  void writeMany() throws IOException {
    // When
    var writeAllMono = RFiles.write(FILES);
    // Then
    StepVerifier.create(writeAllMono).expectNext(FILES).verifyComplete();
    // And
    var writtenPaths = List.ofAll(Files.list(TestUtils.EMPTY_DIR).sorted().toList());
    var expectedWrittenFiles = FILES.map(RFile::path);
    Assertions.assertEquals(expectedWrittenFiles, writtenPaths);
  }
}
