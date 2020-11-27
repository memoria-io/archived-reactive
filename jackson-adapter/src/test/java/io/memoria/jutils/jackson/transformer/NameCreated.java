package io.memoria.jutils.jackson.transformer;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.Meta;
import io.memoria.jutils.core.value.Id;

public record NameCreated(Id id, String name,Meta meta) implements Event {}
