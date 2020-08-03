package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventReadRepo;
import io.memoria.jutils.core.eventsourcing.event.EventWriteRepo;
import io.memoria.jutils.core.eventsourcing.state.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryEventReadRepoTest {
  private static record Greeting() implements State {}

  private static record GreetingEvent(String value) implements Event<Greeting> {}

  private final Map<Integer, Queue<GreetingEvent>> db = new HashMap<>();
  private final EventReadRepo<Integer, GreetingEvent> readRepo = new InMemoryEventReadRepo<>(db);
  private final EventWriteRepo<Integer, GreetingEvent> writeRepo = new InMemoryEventWriteRepo<>(db);
  private final GreetingEvent e1 = new GreetingEvent("hello");
  private final GreetingEvent e2 = new GreetingEvent("Bye");
  private final GreetingEvent e3 = new GreetingEvent("Ciao");

  @Test
  public void add() {
    writeRepo.add(1, e1).block();
    writeRepo.add(2, e2).block();
    assertThat(db.get(0)).isNull();
    assertThat(db.get(1).peek()).isEqualTo(e1);
    assertThat(db.get(2).peek()).isEqualTo(e2);
  }

  @BeforeEach
  public void beforeEach() {
    db.clear();
  }

  @Test
  public void exists() {
    writeRepo.add(1, e1).block();
    assertThat(readRepo.exists(1).block()).isTrue();
  }

  @Test
  public void stream() {
    writeRepo.add(0, e1).block();
    writeRepo.add(0, e2).block();
    writeRepo.add(0, e3).block();
    StepVerifier.create(readRepo.stream(0)).expectNext(e1, e2, e3).expectComplete().verify();
  }
}
