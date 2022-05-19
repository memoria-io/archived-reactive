package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateDecider;
import io.memoria.reactive.core.eventsourcing.socialnetwork.User.Account;
import io.memoria.reactive.core.eventsourcing.socialnetwork.User.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.MarkMsgAsSeen;
import io.vavr.control.Try;

record UserStateDecider() implements StateDecider {
  @Override
  public Try<Event> apply(State state, Command command) {
    if (state instanceof User user && command instanceof UserCommand userCommand) {
      return apply(user, userCommand);
    }
    return Try.failure(UnknownCommand.create(command));

  }

  private Try<Event> apply(User user, UserCommand userCommand) {
    return switch (userCommand) {
      case CreateUser cmd && user instanceof Visitor -> userCreated(cmd);
      case CreateOutboundMsg cmd && user instanceof Account -> outboundCreated(cmd);
      case CreateInboundMsg cmd && user instanceof Account -> inboundMessageCreated(cmd);
      case MarkMsgAsSeen cmd && user instanceof Account -> outboundSeen(cmd);
      case CloseAccount cmd && user instanceof Account -> accountClosed(cmd);
      default -> Try.failure(InvalidOperation.create(user, userCommand));
    };
  }

  private Try<Event> userCreated(CreateUser cmd) {
    return Try.success(UserEvent.userCreated(cmd.commandId(), cmd.userId(), cmd.username()));
  }

  private Try<Event> outboundCreated(CreateOutboundMsg cmd) {
    return Try.success(UserEvent.outboundMsgCreated(cmd.commandId(), cmd.msgSender(), cmd.msgReceiver(), cmd.message()));
  }

  private Try<Event> inboundMessageCreated(CreateInboundMsg cmd) {
    return Try.success(UserEvent.inboundMsgCreated(cmd.commandId(), cmd.msgSender(), cmd.msgReceiver(), cmd.message()));
  }

  private Try<Event> outboundSeen(MarkMsgAsSeen cmd) {
    return Try.success(UserEvent.outboundSeen(cmd.commandId(), cmd.msgSender(), cmd.msgReceiver()));
  }

  private Try<Event> accountClosed(CloseAccount cmd) {
    return Try.success(UserEvent.accountClosed(cmd.commandId(), cmd.userId()));
  }
}
