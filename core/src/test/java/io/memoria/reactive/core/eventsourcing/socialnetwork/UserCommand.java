package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface UserCommand extends Command {

  @Override
  default long timestamp() {
    return 0;
  }

  static CloseAccount closeAccount(StateId userId) {
    return new CloseAccount(CommandId.randomUUID(), userId);
  }

  static CreateInboundMsg createInboundMsg(StateId msgSender, StateId msgReceiver, String message) {
    return new CreateInboundMsg(CommandId.randomUUID(), msgSender, msgReceiver, message);
  }

  static CreateNewMsgNotification createNewMsgNotification(StateId msgReceiver) {
    return new CreateNewMsgNotification(CommandId.randomUUID(), msgReceiver);
  }

  static CreateOutboundMsg createOutboundMsg(StateId msgSender, StateId msgReceiver, String message) {
    return new CreateOutboundMsg(CommandId.randomUUID(), msgSender, msgReceiver, message);
  }

  static CreateUser createUser(StateId userId, String username) {
    return new CreateUser(CommandId.randomUUID(), userId, username);
  }

  static MarkMsgAsSeen markMsgAsSeen(StateId msgSender, StateId msgReceiver) {
    return new MarkMsgAsSeen(CommandId.randomUUID(), msgSender, msgReceiver);
  }

  record CloseAccount(CommandId commandId, StateId userId) implements UserCommand {
    @Override
    public StateId stateId() {
      return userId;
    }
  }

  record CreateInboundMsg(CommandId commandId, StateId msgSender, StateId msgReceiver, String message) implements UserCommand {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record CreateNewMsgNotification(CommandId commandId, StateId msgReceiver) implements UserCommand {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record CreateOutboundMsg(CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements UserCommand {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record CreateUser(CommandId commandId, StateId userId, String username) implements UserCommand {
    @Override
    public StateId stateId() {
      return userId;
    }
  }

  record MarkMsgAsSeen(CommandId commandId, StateId msgSender, StateId msgReceiver) implements UserCommand {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }
}
