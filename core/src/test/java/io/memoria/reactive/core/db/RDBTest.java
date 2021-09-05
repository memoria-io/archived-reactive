package io.memoria.reactive.core.db;

import io.memoria.reactive.core.db.file.FileRDB;
import io.memoria.reactive.core.db.mem.MemRDB;
import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.vavr.collection.List;
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
  private static final int TO = 100;
  private static final String TOPIC = "some_topic";
  private static final Path TOPIC_PATH = Path.of("/tmp/" + TOPIC);
  // DATA
  private static final List<MessageReceived> EVENT_LIST = MessageReceived.create(FROM, TO);
  private static final Flux<MessageReceived> EVENT_FLUX = Flux.fromIterable(EVENT_LIST);
  private static final MessageReceived[] EVENT_ARRAY = EVENT_LIST.toJavaArray(MessageReceived[]::new);
  // RDBs
  private static final MemRDB<MessageReceived> memRDB;
  private static final FileRDB<MessageReceived> fileRDB;

  static {
    var db = new ArrayList<Msg<MessageReceived>>();
    memRDB = new MemRDB<>(db);
    fileRDB = new FileRDB<>(TOPIC, TOPIC_PATH, new SerializableTransformer(), MessageReceived.class);
  }

  @BeforeEach
  void beforeEach() {
    memRDB.db().clear();
    RFiles.clean(fileRDB.path());
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void publish(RDB<MessageReceived> repo) {
    // When
    repo.index().map(i -> )
    var publish = repo.publish(EVENT_FLUX);
    // Then
    StepVerifier.create(publish).expectNext(msgs.toArray(M[]::new)).verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("rdb")
  void subscribe(RDB<MessageReceived> repo) {
    // Given
    var msgs = createMsgs();
    var rdb = new MemRDB<>(msgs.toJavaList());
    var expectedEvents = msgs.toJavaArray(Msg[]::new);
    // When
    var sub = rdb.subscribe(0);
    // Then
    StepVerifier.create(sub).expectNext(expectedEvents).verifyComplete();
  }

  private List<Msg<MessageReceived>> createMsgs() {
    return List.range(0, 100).map(i -> new Msg<>(i, new MessageReceived(Id.of(i), "hello:" + i)));
  }

  private static Stream<RDB<MessageReceived>> rdb() {
    return Stream.of(memRDB, fileRDB);
  }
}
