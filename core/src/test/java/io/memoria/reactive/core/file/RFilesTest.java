package io.memoria.reactive.core.file;

import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.LinkedHashMap;
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

import static io.memoria.reactive.core.file.Utils.FILES_TUPLE;
import static io.memoria.reactive.core.file.Utils.FILES_ARR;
import static io.memoria.reactive.core.file.Utils.FILES_LIST;

class RFilesTest {

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(Utils.EMPTY_DIR).subscribe();
    RFiles.clean(Utils.EMPTY_DIR).subscribe();
  }

  @Test
  @DisplayName("Should append or create a file")
  void create() throws IOException {
    // When
    var writeFileMono = RFiles.write(Utils.SOME_FILE, "hello world");
    StepVerifier.create(writeFileMono).expectNext(Utils.SOME_FILE).verifyComplete();
    // Then
    var str = new String(Files.readAllBytes(Utils.SOME_FILE));
    Assertions.assertEquals(str, "hello world");
  }

  @Test
  void delete() throws IOException {
    // Given
    Files.createFile(Utils.SOME_FILE);
    // When
    var deleteFile = RFiles.delete(Utils.SOME_FILE);
    // Then
    StepVerifier.create(deleteFile).expectNext(Utils.SOME_FILE).verifyComplete();
  }

  @Test
  void deleteAll() throws IOException, InterruptedException {
    // Given
    var files = Utils.writeFiles();
    // When
    var deleteFiles = RFiles.delete(HashSet.ofAll(files));
    // Then
    StepVerifier.create(deleteFiles).expectNextCount(1).verifyComplete();
  }

  @Test
  void lastFile() throws IOException, InterruptedException {
    // Given
    var files = Utils.writeFiles();
    // When
    var lastFile = RFiles.lastFile(Utils.EMPTY_DIR);
    // Then
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void read() throws IOException {
    // Given
    Files.writeString(Utils.SOME_FILE, "welcome");
    // When
    var read = RFiles.read(Utils.SOME_FILE);
    // Then
    StepVerifier.create(read).expectNext("welcome").verifyComplete();
  }

  @Test
  void readDirectory() throws IOException, InterruptedException {
    // Given
    Utils.writeFiles();
    // When
    var readDir = RFiles.readDir(Utils.EMPTY_DIR).map(LinkedHashMap::keySet);
    // Then
    StepVerifier.create(readDir).expectNext(FILES_LIST.toSet()).verifyComplete();
  }

  @Test
  void subscribe() throws IOException, InterruptedException {
    // Given
    Utils.writeFiles();
    // When
    RFiles.readDir(Utils.EMPTY_DIR);
    var sub = RFiles.subscribe(Utils.EMPTY_DIR, Utils.START).map(Tuple2::_1).take(Utils.END);
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
    Assertions.assertEquals(Files.list(Utils.EMPTY_DIR).sorted().toList(), expected);
  }
}
