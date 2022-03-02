package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

public sealed interface UserCommand extends Command {
  @Override
  default Id id() {
    return Id.of(0);
  }

  @Override
  default long timestamp() {
    return 0;
  }

  record CreateInboundMsg(Id userId, Id from, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record CreateOutboundMsg(Id userId, Id to, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record CreateUser(Id userId, String username) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record MarkMsgAsSeen(Id userId, Id seenBy) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }
}
