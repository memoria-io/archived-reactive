package io.memoria.jutils.jcore.eventsourcing.user;

import io.memoria.jutils.jcore.eventsourcing.Command;
import io.memoria.jutils.jcore.id.Id;

public interface UserCommand extends Command {

  record CreateUser(Id commandId, Id aggId, Id userId, String username) implements UserCommand {}

  record SendMessage(Id commandId, Id aggId, Id userId, Id receiverId, String message) implements UserCommand {}
}
