package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {

  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  @Override
  default long timestamp() {
    return 0;
  }

  record ChangeName(CommandId commandId, StateId accountId, String newName) implements AccountCommand {
    public static ChangeName of(StateId accountId, String newName) {
      return new ChangeName(CommandId.randomUUID(), accountId, newName);
    }
  }

  record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {

  }

  record CreatePerson(CommandId commandId, StateId accountId, String accName) implements AccountCommand {
    public static CreatePerson of(StateId accountId, String accountName) {
      return new CreatePerson(CommandId.randomUUID(), accountId, accountName);
    }
  }
}
