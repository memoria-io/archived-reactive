package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.StateId;

public record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {
  public static CloseAccount of(StateId accountId) {
    return new CloseAccount(CommandId.randomUUID(), accountId);
  }
}
