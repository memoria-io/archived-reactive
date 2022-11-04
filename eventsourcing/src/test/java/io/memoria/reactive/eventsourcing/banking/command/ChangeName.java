package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.StateId;

public record ChangeName(StateId accountId, CommandId commandId, String name) implements AccountCommand {}
