package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryEventRepoTest {
  private static record GreetingEvent(String value) implements Event {}

  private final Map<Integer, Queue<Event>> db = new HashMap<>();
  private final EventRepo<Integer, Event> repo = new MemoryEventRepo<>(db);
  private final Event e1 = new GreetingEvent("hello");
  private final Event e2 = new GreetingEvent("Bye");
  private final Event e3 = new GreetingEvent("Ciao");

  @Test
  public void add() {
    repo.add(1, e1).block();
    repo.add(2, e2).block();
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
    repo.add(1, e1).block();
    assertThat(repo.exists(1).block()).isTrue();
  }

  @Test
  public void stream() {
    repo.add(0, e1).block();
    repo.add(0, e2).block();
    repo.add(0, e3).block();
    StepVerifier.create(repo.stream(0)).expectNext(e1, e2, e3).expectComplete().verify();
  }

}
