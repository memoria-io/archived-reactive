package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.StreamDB;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.LocalDateTime;

class RFileStreamDBTest {
  private static final long FROM = 0;
  private static final long TO = 100;
  private static final Path STREAM_DIR_PATH;
  private static final String TOPIC;
  private static final TextTransformer transformer;
  private static final Function1<String, Try<SomeEvent>> DESERIALIZE;
  private static final List<SomeEvent> EVENT_LIST;
  private static final Flux<SomeEvent> EVENT_FLUX;
  private static final SomeEvent[] EVENT_ARRAY;
  private static final String[] EVENT_MSG_ARRAY;
  private static final StreamDB<SomeEvent> eventStreamDB;

  static {
    STREAM_DIR_PATH = Path.of("/tmp/streamDir");
    TOPIC = "some_topic";
    transformer = new SerializableTransformer();
    DESERIALIZE = transformer.deserialize(SomeEvent.class);
    EVENT_LIST = SomeEvent.createBatch();
    EVENT_FLUX = Flux.fromIterable(EVENT_LIST);
    EVENT_ARRAY = EVENT_LIST.toJavaArray(SomeEvent[]::new);
    EVENT_MSG_ARRAY = EVENT_LIST.map(SomeEvent::msg).toJavaArray(String[]::new);
    eventStreamDB = new RFileStreamDB<>(STREAM_DIR_PATH, TOPIC, transformer, SomeEvent.class);
  }

  @BeforeEach
  void beforeEach() {
    RFiles.createDirectory(STREAM_DIR_PATH).subscribe();
    RFiles.clean(STREAM_DIR_PATH).subscribe();
  }

  @Test
  void publish() {
    // When
    eventStreamDB.publish(EVENT_FLUX).subscribe();
    // Then
    var fileIndices = RFileStreamDBUtils.sortedList(STREAM_DIR_PATH);
    var expectedIndices = List.range(FROM, TO).toJavaArray(Long[]::new);
    StepVerifier.create(fileIndices).expectNext(expectedIndices).verifyComplete();
    // And
    var eventFlux = RFileStreamDBUtils.sortedContent(STREAM_DIR_PATH).map(DESERIALIZE).map(Try::get);
    StepVerifier.create(eventFlux).expectNext(EVENT_ARRAY).verifyComplete();
  }

  private static record SomeEvent(Id eventId, String msg) implements Event {
    @Override
    public Id aggId() {
      return Id.of(TOPIC);
    }

    @Override
    public LocalDateTime createdAt() {
      return LocalDateTime.of(2111, 1, 1, 1, 1);
    }

    static List<SomeEvent> createBatch() {
      return List.range(FROM, TO).map(i -> new SomeEvent(Id.of(i), "hello" + i));
    }
  }
}
