package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.reactive.core.eventsourcing.ESException.UnknownCommand;
import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.UserCommand.MarkMsgAsSeen;
import io.memoria.reactive.core.eventsourcing.UserEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundSeen;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.state.StateDecider;
import io.vavr.control.Try;

public record UserStateDecider() implements StateDecider {
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
      default -> Try.failure(InvalidOperation.create(user, userCommand));
    };
  }

  private Try<Event> inboundMessageCreated(CreateInboundMsg cmd) {
    return Try.success(new InboundMsgCreated(cmd.userId(), cmd.from(), cmd.message()));
  }

  private Try<Event> outboundCreated(CreateOutboundMsg cmd) {
    return Try.success(new OutboundMsgCreated(cmd.userId(), cmd.to(), cmd.message()));
  }

  private Try<Event> outboundSeen(MarkMsgAsSeen cmd) {
    return Try.success(new OutboundSeen(cmd.userId(), cmd.seenBy()));
  }

  private Try<Event> userCreated(CreateUser cmd) {
    return Try.success(new UserCreated(cmd.userId(), cmd.username()));
  }
}
