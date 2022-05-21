package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountEvent extends Event {

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

  static AccountCreated userCreated(CommandId commandId, StateId userId, String name) {
    return new AccountCreated(EventId.randomUUID(), commandId, userId, name);
  }

  record AccountClosed(EventId eventId, CommandId commandId, StateId userId) implements AccountEvent {
    @Override
    public StateId stateId() {
      return userId;
    }
  }

  record InboundMsgCreated(EventId eventId, CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record OutboundMsgCreated(EventId eventId,
                            CommandId commandId,
                            StateId msgSender,
                            StateId msgReceiver,
                            String message) implements AccountEvent {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record OutboundSeen(EventId eventId, CommandId commandId, StateId msgSender, StateId msgReceiver)
          implements AccountEvent {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record AccountCreated(EventId eventId, CommandId commandId, StateId userId, String name) implements AccountEvent {
    @Override
    public StateId stateId() {
      return userId;
    }
  }
}
