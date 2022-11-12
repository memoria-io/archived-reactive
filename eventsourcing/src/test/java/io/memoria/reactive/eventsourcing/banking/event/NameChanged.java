package io.memoria.reactive.eventsourcing.banking.event;

import io.memoria.atom.eventsourcing.CommandId;
import io.memoria.atom.eventsourcing.EventId;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.ChangeName;

public record NameChanged(EventId eventId, CommandId commandId, StateId accountId, String newName)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static NameChanged from(ChangeName cmd) {
    return new NameChanged(EventId.randomUUID(), cmd.commandId(), cmd.stateId(), cmd.name());
  }
}
