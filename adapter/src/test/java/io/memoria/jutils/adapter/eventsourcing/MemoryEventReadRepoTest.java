package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventReadRepo;
import io.memoria.jutils.core.eventsourcing.event.EventWriteRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryEventReadRepoTest {
  private static record GreetingCreated(String eventId, String aggId, String value) implements Event {}

  private final Map<Integer, Queue<GreetingCreated>> db = new HashMap<>();
  private final EventReadRepo<Integer, GreetingCreated> readRepo = new InMemoryEventReadRepo<>(db);
  private final EventWriteRepo<Integer, GreetingCreated> writeRepo = new InMemoryEventWriteRepo<>(db);
  private final GreetingCreated e1 = new GreetingCreated("0", "0", "hello");
  private final GreetingCreated e2 = new GreetingCreated("1", "1", "Bye");
  private final GreetingCreated e3 = new GreetingCreated("2", "1", "Ciao");

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
