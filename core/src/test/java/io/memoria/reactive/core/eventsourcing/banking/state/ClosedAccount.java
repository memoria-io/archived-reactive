package io.memoria.reactive.core.eventsourcing.banking.state;

import io.memoria.reactive.core.eventsourcing.StateId;

public record ClosedAccount(StateId accountId) implements Account {}
