package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface UserEvent extends Event {

  @Override
  default long timestamp() {
    return 0;
  }

  static AccountClosed accountClosed(CommandId commandId, StateId userId) {
    return new AccountClosed(EventId.randomUUID(), commandId, userId);
  }

  static InboundMsgCreated inboundMsgCreated(CommandId commandId,
                                             StateId msgSender,
                                             StateId msgReceiver,
                                             String message) {
    return new InboundMsgCreated(EventId.randomUUID(), commandId, msgSender, msgReceiver, message);
  }

  static OutboundMsgCreated outboundMsgCreated(CommandId commandId,
                                               StateId msgSender,
                                               StateId msgReceiver,
                                               String message) {
    return new OutboundMsgCreated(EventId.randomUUID(), commandId, msgSender, msgReceiver, message);
  }

  static OutboundSeen outboundSeen(CommandId commandId, StateId msgSender, StateId msgReceiver) {
    return new OutboundSeen(EventId.randomUUID(), commandId, msgSender, msgReceiver);
  }

  static UserCreated userCreated(CommandId commandId, StateId userId, String name) {
    return new UserCreated(EventId.randomUUID(), commandId, userId, name);
  }

  record AccountClosed(EventId id, CommandId commandId, StateId userId) implements UserEvent {
    @Override
    public StateId stateId() {
      return userId;
    }
  }

  record InboundMsgCreated(EventId id, CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements UserEvent {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record OutboundMsgCreated(EventId id, CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements UserEvent {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record OutboundSeen(EventId id, CommandId commandId, StateId msgSender, StateId msgReceiver) implements UserEvent {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record UserCreated(EventId id, CommandId commandId, StateId userId, String name) implements UserEvent {
    @Override
    public StateId stateId() {
      return userId;
    }
  }
}
