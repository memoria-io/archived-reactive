package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.id.Id;

public interface UserCommand extends Command {
  @Override
  default Id id(){
    return Id.of(0);
  }

  record CreateUser(Id userId, String username) implements UserCommand {
    @Override
    public Id aggId() {
      return userId;
    }
  }

  record SendMessage(Id userId, Id receiverId, String message) implements UserCommand {
    @Override
    public Id aggId() {
      return userId;
    }
  }
}
