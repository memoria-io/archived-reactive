package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;

import java.util.UUID;

sealed interface UserEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  static AccountClosed accountClosed(Id commandId, Id userId) {
    return new AccountClosed(Id.of(UUID.randomUUID()), commandId, userId);
  }

  static InboundMsgCreated inboundMsgCreated(Id commandId, Id msgSender, Id msgReceiver, String message) {
    return new InboundMsgCreated(Id.of(UUID.randomUUID()), commandId, msgSender, msgReceiver, message);
  }

  static OutboundMsgCreated outboundMsgCreated(Id commandId, Id msgSender, Id msgReceiver, String message) {
    return new OutboundMsgCreated(Id.of(UUID.randomUUID()), commandId, msgSender, msgReceiver, message);
  }

  static OutboundSeen outboundSeen(Id commandId, Id msgSender, Id msgReceiver) {
    return new OutboundSeen(Id.of(UUID.randomUUID()), commandId, msgSender, msgReceiver);
  }

  static UserCreated userCreated(Id commandId, Id userId, String name) {
    return new UserCreated(Id.of(UUID.randomUUID()), commandId, userId, name);
  }

  record AccountClosed(Id id, Id commandId, Id userId) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record InboundMsgCreated(Id id, Id commandId, Id msgSender, Id msgReceiver, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return msgReceiver;
    }
  }

  record OutboundMsgCreated(Id id, Id commandId, Id msgSender, Id msgReceiver, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return msgSender;
    }
  }

  record OutboundSeen(Id id, Id commandId, Id msgSender, Id msgReceiver) implements UserEvent {
    @Override
    public Id stateId() {
      return msgSender;
    }
  }

  record UserCreated(Id id, Id commandId, Id userId, String name) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }
}
