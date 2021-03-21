package io.memoria.jutils.jcore.eventsourcing.data.user;

import io.memoria.jutils.jcore.eventsourcing.Command;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;

public interface UserCommand extends Command {

  record CreateUser(Id commandId, Id aggId, String username) implements UserCommand {
    public static List<UserCommand> createMany(String topic, long i) {
      int factor = 100;
      return List.range((i * factor) + 1, (i * factor) + factor).map(j -> new CreateUser(j, topic));
    }

    public CreateUser(long eventId, String name) {
      this(Id.of(eventId), Id.of(name), "username" + eventId);
    }
  }
}
