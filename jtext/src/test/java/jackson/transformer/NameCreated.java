package jackson.transformer;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.id.Id;

import java.time.LocalDateTime;

public record NameCreated(Id eventId, Id aggId, String name, LocalDateTime createdAt) implements Event {}
