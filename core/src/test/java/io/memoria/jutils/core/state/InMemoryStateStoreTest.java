package io.memoria.jutils.core.state;

import io.memoria.jutils.core.eventsourcing.state.BlockingStateStore;
import io.memoria.jutils.core.eventsourcing.state.InMemoryStateStore;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class InMemoryStateStoreTest {
  private static record Person(Id id, int age) implements State {
    public Person withAge(int age) {
      return new Person(this.id, age);
    }
  }

  private final ConcurrentMap<Id, Person> db = new ConcurrentHashMap<>();
  private final BlockingStateStore<Person> store = new InMemoryStateStore<>(db);

  @BeforeEach
  void beforeEach() {
    db.clear();
  }

  @Test
  void concurrentSave() throws InterruptedException, TimeoutException, ExecutionException {
    var id = new Id("same_id");
    var es = Executors.newFixedThreadPool(10);
    store.save(new Person(id, 0));
    for (int i = 0; i < 10; i++) {
      es.submit(() -> {
        synchronized (store) {
          var s = store.get(id).get();
          var age = s.age;
          store.save(s.withAge(++age));
        }
      });
    }
    es.awaitTermination(1, TimeUnit.SECONDS);
  }
}
