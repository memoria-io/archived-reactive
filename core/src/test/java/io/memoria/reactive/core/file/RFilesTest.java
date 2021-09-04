package io.memoria.reactive.core.file;

import io.vavr.Tuple;
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

class RFilesTest {
  private static final Path emptyDir = Path.of("/tmp/emptyDir");
  private static final Path someFilePath = emptyDir.resolve("appendOrCreate.txt");
  private static final int START = 0;
  private static final int END = 3;

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(emptyDir).subscribe();
    RFiles.clean(emptyDir).subscribe();
  }

  @Test
  @DisplayName("Should append or create a file")
  void create() throws IOException {
    // When
    var writeFileMono = RFiles.write(someFilePath, "hello world");
    StepVerifier.create(writeFileMono).expectNext(someFilePath).expectComplete().verify();
    // Then
    var str = new String(Files.readAllBytes(someFilePath));
    Assertions.assertEquals(str, "hello world");
  }

  @Test
  void delete() throws IOException {
    // Given
    Files.createFile(someFilePath);
    // When
    var deleteFile = RFiles.delete(someFilePath);
    // Then
    StepVerifier.create(deleteFile).expectNext(someFilePath).verifyComplete();
  }

  @Test
  void deleteAll() throws IOException, InterruptedException {
    // Given
    var files = writeFiles();
    // When
    var deleteFiles = RFiles.delete(HashSet.ofAll(files));
    // Then
    StepVerifier.create(deleteFiles).expectNextCount(1).verifyComplete();
  }

  @Test
  void lastFile() throws IOException, InterruptedException {
    // Given
    var files = writeFiles();
    // When
    var lastFile = RFiles.lastFile(emptyDir);
    // Then
    StepVerifier.create(lastFile).expectNext(files.last()).verifyComplete();
  }

  @Test
  void read() throws IOException {
    // Given
    Files.writeString(someFilePath, "welcome");
    // When
    var read = RFiles.read(someFilePath);
    // Then
    StepVerifier.create(read).expectNext("welcome").verifyComplete();
  }

  @Test
  void readDirectory() throws IOException, InterruptedException {
    // Given
    var files = writeFiles();
    var expected = files.toSet();
    // When
    var readDir = RFiles.readDir(emptyDir).map(LinkedHashMap::keySet);
    // Then
    StepVerifier.create(readDir).expectNext(expected).verifyComplete();
  }

  @Test
  void subscribe() throws IOException, InterruptedException {
    // Given
    var files = writeFiles();
    var expected = files.toJavaArray(Path[]::new);
    // When
    RFiles.readDir(emptyDir);
    var sub = RFiles.subscribe(emptyDir, START).map(Tuple2::_1).take(END);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  @Test
  void publish() {
    // Given
    var files = Flux.fromIterable(files());
    var expected = files().map(Tuple2::_1).toJavaArray(Path[]::new);
    // When
    var pub = RFiles.publish(files);
    // Then
    StepVerifier.create(pub).expectNext(expected).verifyComplete();
  }

  @Test
  void writeMany() throws IOException {
    // Given
    var lMap = files().toLinkedMap(Function.identity());
    var expected = files().map(Tuple2::_1).toJavaList();
    // When
    var writeAll = RFiles.write((LinkedHashMap<Path, String>) lMap);
    // Then
    StepVerifier.create(writeAll).expectNextCount(1).verifyComplete();
    Assertions.assertEquals(Files.list(emptyDir).sorted().toList(), expected);
  }

  private List<Tuple2<Path, String>> files() {
    return List.range(START, END)
               .map(i -> Tuple.of(emptyDir.resolve("file%d.txt".formatted(i)), "hello world%d".formatted(i)));
  }

  private List<Path> writeFiles() throws IOException, InterruptedException {
    var files = files();
    for (Tuple2<Path, String> tup : files) {
      Files.writeString(tup._1, tup._2);
      Thread.sleep(10);
    }
    return files.map(Tuple2::_1);
  }
}
