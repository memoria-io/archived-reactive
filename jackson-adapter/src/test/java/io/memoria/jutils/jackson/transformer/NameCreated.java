package io.memoria.jutils.jackson.transformer;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.core.value.Version;

import java.time.LocalDateTime;

public record NameCreated(Id id, String name, LocalDateTime createdAt, Version version) implements Event {}
