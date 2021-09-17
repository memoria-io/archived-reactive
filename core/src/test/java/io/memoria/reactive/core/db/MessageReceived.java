package io.memoria.reactive.core.db;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

import java.time.LocalDateTime;

record MessageReceived(String msg) implements Event {
  @Override
  public Id aggId() {
    return Id.of("aggId");
  }

  @Override
  public LocalDateTime createdAt() {
    return LocalDateTime.of(2111, 1, 1, 1, 1);
  }

  @Override
  public Id id() {
    return Id.of(0);
  }

  static List<MessageReceived> create(int from, int to) {
    return List.range(from, to).map(i -> new MessageReceived("hello" + i));
  }
}
