package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;

interface UserCommand extends Command {

  record CreateUser(Id commandId, Id aggId, String username) implements UserCommand {
    public static List<UserCommand> createMany(String topic, long i) {
      int factor = 100;
      return List.range((i * factor) + 1, (i * factor) + factor).map(j -> new CreateUser(j, topic));
    }

    public CreateUser(long eventId, String topic) {
      this(Id.of(eventId), Id.of(topic), "username" + eventId);
    }
  }
}
