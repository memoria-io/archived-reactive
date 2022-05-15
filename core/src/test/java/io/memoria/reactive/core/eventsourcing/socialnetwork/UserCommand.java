package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface UserCommand extends Command {

  @Override
  default long timestamp() {
    return 0;
  }

  static CloseAccount closeAccount(Id userId) {
    return new CloseAccount(Id.of(UUID.randomUUID()), userId);
  }

  static CreateInboundMsg createInboundMsg(Id msgSender, Id msgReceiver, String message) {
    return new CreateInboundMsg(Id.of(UUID.randomUUID()), msgSender, msgReceiver, message);
  }

  static CreateNewMsgNotification createNewMsgNotification(Id msgReceiver) {
    return new CreateNewMsgNotification(Id.of(UUID.randomUUID()), msgReceiver);
  }

  static CreateOutboundMsg createOutboundMsg(Id msgSender, Id msgReceiver, String message) {
    return new CreateOutboundMsg(Id.of(UUID.randomUUID()), msgSender, msgReceiver, message);
  }

  static CreateUser createUser(Id userId, String username) {
    return new CreateUser(Id.of(UUID.randomUUID()), userId, username);
  }

  static MarkMsgAsSeen markMsgAsSeen(Id msgSender, Id msgReceiver) {
    return new MarkMsgAsSeen(Id.of(UUID.randomUUID()), msgSender, msgReceiver);
  }

  record CloseAccount(Id id, Id userId) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record CreateInboundMsg(Id id, Id msgSender, Id msgReceiver, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return msgReceiver;
    }
  }

  record CreateNewMsgNotification(Id id, Id msgReceiver) implements UserCommand {
    @Override
    public Id stateId() {
      return msgReceiver;
    }
  }

  record CreateOutboundMsg(Id id, Id msgSender, Id msgReceiver, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return msgSender;
    }
  }

  record CreateUser(Id id, Id userId, String username) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record MarkMsgAsSeen(Id id, Id msgSender, Id msgReceiver) implements UserCommand {
    @Override
    public Id stateId() {
      return msgSender;
    }
  }
}
