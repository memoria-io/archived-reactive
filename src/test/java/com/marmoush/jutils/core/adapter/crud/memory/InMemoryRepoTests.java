package com.marmoush.jutils.core.adapter.crud.memory;

import com.marmoush.jutils.core.domain.entity.Entity;
import com.marmoush.jutils.core.domain.port.crud.EntityRepo;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;
import reactor.test.StepVerifier;

import java.util.*;

import static com.marmoush.jutils.core.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.core.domain.error.NotFound.NOT_FOUND;

public class InMemoryRepoTests {
  private final Map<String, Entity<String>> db = new HashMap<>();
  private final EntityRepo<Entity<String>> repo = new InMemoryRepo<>(db);
  private final Entity<String> entity = new Entity<>("id", "value");
  private final Entity<String> updateEntity = new Entity<>("id", "other_value");

  @AfterEach
  public void afterEach() {
    db.clear();
  }

  @Test
  @DisplayName("Should crud the entity")
  public void crudTest() {
    StepVerifier.create(repo.create(entity)).expectNextMatches(Try::isSuccess).expectComplete().verify();
    Assertions.assertEquals("value", db.get("id").value);
    StepVerifier.create(repo.update(updateEntity)).expectNextMatches(Try::isSuccess).expectComplete().verify();
    StepVerifier.create(repo.delete(entity.id)).expectComplete().verify();
  }

  @Test
  @DisplayName("Should be not found")
  public void notFoundTest() {
    StepVerifier.create(repo.update(entity))
                .expectNextMatches(s -> s.getCause().equals(NOT_FOUND))
                .expectComplete()
                .verify();
    StepVerifier.create(repo.delete("ids")).expectComplete().verify();
  }

  @Test
  @DisplayName("Already exists")
  public void alreadyExists() {
    db.put(this.entity.id, this.entity);
    StepVerifier.create(repo.create(entity))
                .expectNextMatches(s -> s.getCause().equals(ALREADY_EXISTS))
                .expectComplete()
                .verify();
    StepVerifier.create(repo.delete("ids")).expectComplete().verify();
  }
}