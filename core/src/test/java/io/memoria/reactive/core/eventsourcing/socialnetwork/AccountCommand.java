package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {

  @Override
  default long timestamp() {
    return 0;
  }

  static CloseAccount closeAccount(StateId accountId) {
    return new CloseAccount(CommandId.randomUUID(), accountId);
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

  static CreateAcc createAcc(StateId accountId, String accountname) {
    return new CreateAcc(CommandId.randomUUID(), accountId, accountname);
  }

  static MarkMsgAsSeen markMsgAsSeen(StateId msgSender, StateId msgReceiver) {
    return new MarkMsgAsSeen(CommandId.randomUUID(), msgSender, msgReceiver);
  }

  record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {
    @Override
    public StateId stateId() {
      return accountId;
    }
  }

  record CreateInboundMsg(CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements AccountCommand {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record CreateNewMsgNotification(CommandId commandId, StateId msgReceiver) implements AccountCommand {
    @Override
    public StateId stateId() {
      return msgReceiver;
    }
  }

  record CreateOutboundMsg(CommandId commandId, StateId msgSender, StateId msgReceiver, String message)
          implements AccountCommand {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }

  record CreateAcc(CommandId commandId, StateId accountId, String accountname) implements AccountCommand {
    @Override
    public StateId stateId() {
      return accountId;
    }
  }

  record MarkMsgAsSeen(CommandId commandId, StateId msgSender, StateId msgReceiver) implements AccountCommand {
    @Override
    public StateId stateId() {
      return msgSender;
    }
  }
}
