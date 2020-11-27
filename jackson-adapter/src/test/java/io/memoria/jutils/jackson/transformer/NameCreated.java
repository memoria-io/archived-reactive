package io.memoria.jutils.jackson.transformer;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.value.Id;

import java.time.LocalDateTime;

public record NameCreated(Id id, String name, LocalDateTime createdAt) implements Event {}
