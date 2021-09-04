package io.memoria.reactive.core.file;

import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static io.memoria.reactive.core.file.TestUtils.FILES_ARR;
import static io.memoria.reactive.core.file.TestUtils.FILES_LIST;
import static io.memoria.reactive.core.file.TestUtils.FILES_TUPLE;

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
    var writeFileMono = RFiles.write(TestUtils.SOME_FILE, "hello world");
    StepVerifier.create(writeFileMono).expectNext(TestUtils.SOME_FILE).verifyComplete();
    // Then
    var str = new String(Files.readAllBytes(TestUtils.SOME_FILE));
    Assertions.assertEquals(str, "hello world");
  }

  @Test
  void delete() throws IOException {
    // Given
    Files.createFile(TestUtils.SOME_FILE);
    // When
    var deleteFile = RFiles.delete(TestUtils.SOME_FILE);
    // Then
    StepVerifier.create(deleteFile).expectNext(TestUtils.SOME_FILE).verifyComplete();
  }

  @Test
  void deleteAll() throws IOException, InterruptedException {
    // Given
    var files = TestUtils.writeFiles();
    // When
    var deleteFiles = RFiles.delete(HashSet.ofAll(files));
    // Then
    StepVerifier.create(deleteFiles).expectNextCount(1).verifyComplete();
  }

  @Test
  void lastFile() throws IOException, InterruptedException {
    // Given
    var files = TestUtils.writeFiles();
    // When
    var lastFile = RFiles.lastModified(TestUtils.EMPTY_DIR);
    // Then
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void read() throws IOException {
    // Given
    Files.writeString(TestUtils.SOME_FILE, "welcome");
    // When
    var read = RFiles.read(TestUtils.SOME_FILE);
    // Then
    StepVerifier.create(read).expectNext("welcome").verifyComplete();
  }

  @Test
  void readDirectory() throws IOException, InterruptedException {
    // Given
    TestUtils.writeFiles();
    // When
    var readDir = RFiles.readDir(TestUtils.EMPTY_DIR)
                        .flatMapMany(Flux::fromIterable)
                        .map(Tuple2::_1)
                        .collectList()
                        .map(List::ofAll)
                        .map(List::toSet);
    // Then
    StepVerifier.create(readDir).expectNext(FILES_LIST.toSet()).verifyComplete();
  }

  @Test
  void subscribe() throws IOException, InterruptedException {
    // Given
    TestUtils.writeFiles();
    // When
    RFiles.readDir(TestUtils.EMPTY_DIR);
    var sub = RFiles.subscribe(TestUtils.EMPTY_DIR, TestUtils.START).map(Tuple2::_1).take(
            TestUtils.END);
    // Then
    StepVerifier.create(sub).expectNext(FILES_ARR).verifyComplete();
  }

  @Test
  void publish() {
    // Given
    var files = Flux.fromIterable(FILES_TUPLE);
    // When
    var pub = RFiles.publish(files);
    // Then
    StepVerifier.create(pub).expectNext(FILES_ARR).verifyComplete();
  }

  @Test
  void writeMany() throws IOException {
    // Given
    var lMap = FILES_TUPLE.toLinkedMap(Function.identity());
    var expected = FILES_TUPLE.map(Tuple2::_1).toJavaList();
    // When
    var writeAll = RFiles.write((LinkedHashMap<Path, String>) lMap);
    // Then
    StepVerifier.create(writeAll).expectNextCount(1).verifyComplete();
    Assertions.assertEquals(Files.list(TestUtils.EMPTY_DIR).sorted().toList(), expected);
  }
}
