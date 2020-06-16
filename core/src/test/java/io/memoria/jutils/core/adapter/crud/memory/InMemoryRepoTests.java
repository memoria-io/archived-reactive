package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.port.crud.ReadRepo;
import io.memoria.jutils.core.domain.port.crud.Storable;
import io.memoria.jutils.core.domain.port.crud.WriteRepo;
import io.vavr.control.Try;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static io.memoria.jutils.core.domain.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.NotFound.NOT_FOUND;

public class InMemoryRepoTests {
  private static record User(String id, int age) implements Storable {}

  private final Map<String, Storable> db = new HashMap<>();
  private final ReadRepo<Storable> readRepo = new InMemoryReadRepo<>(db);
  private final WriteRepo<Storable> writeRepo = new InMemoryWriteRepo<>(db);

  private final User user = new User("bob", 20);
  private final User otherUser = new User("bob", 23);

  @AfterEach
  public void afterEach() {
    db.clear();
  }

  @Test
  @DisplayName("Should crud the entity")
  public void crudTest() {
    StepVerifier.create(writeRepo.create(user)).expectNextMatches(Try::isSuccess).expectComplete().verify();
    Assertions.assertEquals(new User("bob", 20), db.get("bob"));
    StepVerifier.create(writeRepo.update(otherUser)).expectNextMatches(Try::isSuccess).expectComplete().verify();
    StepVerifier.create(writeRepo.delete(user.id)).expectComplete().verify();
  }

  @Test
  @DisplayName("Should be not found")
  public void notFoundTest() {
    StepVerifier.create(writeRepo.update(user))
                .expectNextMatches(s -> s.getCause().equals(NOT_FOUND))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("Already exists")
  public void alreadyExists() {
    db.put(this.user.id, this.user);
    StepVerifier.create(writeRepo.create(user))
                .expectNextMatches(s -> s.getCause().equals(ALREADY_EXISTS))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("Should exists")
  public void exists() {
    db.put(this.user.id, this.user);
    StepVerifier.create(readRepo.get(user.id)).expectNext(Try.success(user)).expectComplete().verify();
    StepVerifier.create(readRepo.exists(user.id)).expectNext(Try.success(true)).expectComplete().verify();
    db.clear();
    StepVerifier.create(readRepo.exists(user.id)).expectNext(Try.success(true)).expectComplete().verify();
  }

  @Test
  @DisplayName("Should delete successfully")
  public void delete() {
    db.put(this.user.id, this.user);
    StepVerifier.create(writeRepo.delete(user.id)).expectComplete().verify();
    Assertions.assertNull(db.get(user.id));
  }
}
