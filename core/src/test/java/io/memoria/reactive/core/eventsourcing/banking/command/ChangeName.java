package io.memoria.reactive.core.eventsourcing.banking.command;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record ChangeName(StateId accountId, CommandId commandId, String name) implements AccountCommand {}
