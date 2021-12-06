package io.memoria.reactive.core.rsdb;

import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.rsdb.file.FileRSDB;
import io.memoria.reactive.core.rsdb.mem.MemRSDB;
import io.memoria.reactive.core.text.SerializableTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

class RSDBTest {
  private static final int FROM = 0;
  private static final int TO = 1000;
  private static final String TOPIC = "some_topic";
  private static final Path TOPIC_PATH = Path.of("/tmp/" + TOPIC);

  // RDBs
  private static final MemRSDB<MessageReceived> MEM_RDB;
  private static final FileRSDB<MessageReceived> FILE_RDB;

  static {
    var db = new ArrayList<MessageReceived>();
    MEM_RDB = new MemRSDB<>(db);
    var serDes = new SerializableTransformer();
    FILE_RDB = new FileRSDB<>(0, TOPIC_PATH, serDes::serialize, serDes.deserialize(MessageReceived.class));
  }

  @BeforeEach
  void beforeEach() {
    MEM_RDB.db().clear();
    RFiles.createDirectory(TOPIC_PATH).subscribe();
    RFiles.clean(TOPIC_PATH).subscribe();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void publish(RSDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    var expected = eventList.toJavaArray(MessageReceived[]::new);
    // When
    var publish = Flux.fromIterable(eventList).concatMap(repo::publish);
    // Then
    StepVerifier.create(publish).expectNext(expected).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void read(RSDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    MEM_RDB.db().addAll(eventList.toJavaList());
    Flux.fromIterable(eventList).concatMap(FILE_RDB::publish).subscribe();
    // When
    var read = repo.read(0);
    // Then
    StepVerifier.create(read).expectNext(eventList).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void subscribe(RSDB<MessageReceived> repo) {
    // Given
    var eventList = MessageReceived.create(FROM, TO);
    MEM_RDB.db().addAll(eventList.toJavaList());
    Flux.fromIterable(eventList).concatMap(FILE_RDB::publish).subscribe();
    var expected = eventList.toJavaArray(MessageReceived[]::new);
    // When
    var sub = repo.subscribe(0).take(TO);
    // Then
    StepVerifier.create(sub).expectNext(expected).verifyComplete();
  }

  private static Stream<RSDB<MessageReceived>> rdb() {
    return Stream.of(MEM_RDB, FILE_RDB);
  }
}
