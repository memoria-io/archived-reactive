package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;

import java.time.LocalDateTime;

public record UserCreated(Id eventId, Id aggId, String username, LocalDateTime createdAt) implements Event {
  public static List<Event> createMany(String topic, long i) {
    int factor = 100;
    return List.range((i * factor) + 1, (i * factor) + factor).map(j -> new UserCreated(j, topic));
  }

  public UserCreated(long eventId, String topic) {
    this(Id.of(eventId), Id.of(topic), "username" + eventId, LocalDateTime.of(2020, 10, 10, 10, 10));
  }
}
