package io.memoria.jutils.jtext.jackson.cases.company;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;

import java.time.LocalDateTime;

public record NameCreated(Id eventId, Id aggId, String name, LocalDateTime createdAt) implements Event {}
