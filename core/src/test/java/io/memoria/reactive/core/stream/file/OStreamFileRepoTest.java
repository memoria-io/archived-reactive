package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.FileOps;
import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;

class OStreamFileRepoTest {
  private static final Path TEST_DIR = Path.of("/tmp/rFilesTest");
  private static final OStreamRepo streamRepo = new OStreamFileRepo(TEST_DIR);
  private static final String SOME_TOPIC = "node";

  @BeforeEach
  void beforeEach() {
    FileOps.deleteDir(TEST_DIR).subscribe();
    FileOps.createDir(TEST_DIR).subscribe();
    streamRepo.create(SOME_TOPIC).subscribe();
  }

  @Test
  void publish() {
    // Given
    var msgs = List.range(0, 100).map(i -> new OMsg(i, "hello" + i));
    // When
    var pub = Flux.fromIterable(msgs).flatMap(msg -> streamRepo.publish(SOME_TOPIC, msg));
    // Then
    var expected = msgs.map(OMsg::sKey).toJavaArray(Integer[]::new);
    StepVerifier.create(pub).expectNext(expected).verifyComplete();
  }

  @Test
  void subscribe() {
    // Given
    createSomeFiles(TEST_DIR.resolve(SOME_TOPIC), 0, 100).subscribe();
    createSomeFiles(TEST_DIR.resolve(SOME_TOPIC), 100, 100).delaySubscription(Duration.ofMillis(100)).subscribe();
    // When
    var sub = streamRepo.subscribe(SOME_TOPIC, 0).map(OMsg::sKey).take(200);
    var expected = List.range(0, 200).toJavaArray(Integer[]::new);
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private static Flux<Path> createSomeFiles(Path dir, int start, int count) {
    return Flux.range(start, count).concatMap(i -> TopicDirOps.write(dir, i, "hello world"));
  }
}
