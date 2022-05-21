package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record ChangeName(CommandId commandId, StateId accountId, String newName) implements AccountCommand {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static ChangeName of(StateId accountId, String newName) {
      return new ChangeName(CommandId.randomUUID(), accountId, newName);
    }
  }

  record CreatePerson(CommandId commandId, StateId accountId, String username) implements AccountCommand {
    @Override
    public StateId stateId() {
      return accountId;
    }

    public static CreatePerson of(StateId accountId, String username) {
      return new CreatePerson(CommandId.randomUUID(), accountId, username);
    }
  }

}
