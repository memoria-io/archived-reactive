package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface PersonCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record ChangeName(Id id, Id userId, String newName) implements PersonCommand {
    @Override
    public Id stateId() {
      return userId;
    }

    public static ChangeName of(Id userId, String newName) {
      return new ChangeName(Id.of(UUID.randomUUID()), userId, newName);
    }
  }

  record CreatePerson(Id id, Id userId, String username) implements PersonCommand {
    @Override
    public Id stateId() {
      return userId;
    }

    public static CreatePerson of(Id userId, String username) {
      return new CreatePerson(Id.of(UUID.randomUUID()), userId, username);
    }
  }

}
