package io.memoria.reactive.core.db;

import io.memoria.reactive.core.db.file.FileRDB;
import io.memoria.reactive.core.db.mem.MemRDB;
import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.text.SerializableTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

class RDBTest {
  private static final int FROM = 0;
  private static final int TO = 1000;
  private static final String TOPIC = "some_topic";
  private static final Path TOPIC_PATH = Path.of("/tmp/" + TOPIC);

  // RDBs
  private static final MemRDB<MessageReceived> MEM_RDB;
  private static final FileRDB<MessageReceived> FILE_RDB;

  static {
    var db = new ArrayList<MessageReceived>();
    MEM_RDB = new MemRDB<>(db);
    FILE_RDB = new FileRDB<>(0, TOPIC_PATH, new SerializableTransformer(), MessageReceived.class);
  }

  @BeforeEach
  void beforeEach() {
    MEM_RDB.db().clear();
    RFiles.createDirectory(TOPIC_PATH).subscribe();
    RFiles.clean(TOPIC_PATH).subscribe();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void publish(RDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    var expected = eventList.toJavaArray(MessageReceived[]::new);
    // When
    var publish = repo.publish(Flux.fromIterable(eventList));
    // Then
    StepVerifier.create(publish).expectNext(expected).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void read(RDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    MEM_RDB.db().addAll(eventList.toJavaList());
    FILE_RDB.write(eventList).subscribe();
    // When
    var read = repo.read(0);
    // Then
    StepVerifier.create(read).expectNext(eventList).verifyComplete();
    StepVerifier.create(repo.size()).expectNext(eventList.size()).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void subscribe(RDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    MEM_RDB.db().addAll(eventList.toJavaList());
    FILE_RDB.write(eventList).subscribe();
    var expected = eventList.toJavaArray(MessageReceived[]::new);
    // When
    var sub = repo.subscribe(0).take(TO);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void write(RDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    // When
    var write = repo.write(eventList);
    // Then
    StepVerifier.create(write).expectNext(eventList).verifyComplete();
  }

  private static Stream<RDB<MessageReceived>> rdb() {
    return Stream.of(MEM_RDB, FILE_RDB);
  }
}