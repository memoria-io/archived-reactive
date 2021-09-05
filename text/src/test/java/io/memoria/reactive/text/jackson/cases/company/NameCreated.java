package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.time.LocalDateTime;

public record NameCreated(long id, Id aggId, String name, LocalDateTime createdAt) implements Event {}
