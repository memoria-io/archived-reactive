package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.UserCommand.CreateInboundMessage;
import io.memoria.reactive.core.eventsourcing.UserCommand.MarkMessageAsSeen;
import io.memoria.reactive.core.eventsourcing.UserEvent;
import io.memoria.reactive.core.eventsourcing.UserEvent.InboundMessageCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundMessageCreated;
import io.memoria.reactive.core.eventsourcing.saga.SagaDecider;
import io.vavr.control.Option;

public record UserSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return switch (event) {
      case UserEvent userEvent -> handleUserEvent(userEvent);
      default -> Option.none();
    };
  }

  private Option<Command> handleUserEvent(UserEvent userEvent) {
    return switch (userEvent) {
      case OutboundMessageCreated messageSent -> Option.some(createInboundMessage(messageSent));
      case InboundMessageCreated inboundMessageCreated -> Option.some(markMessageAsSeen(inboundMessageCreated));
      default -> Option.none();
    };
  }

  private MarkMessageAsSeen markMessageAsSeen(InboundMessageCreated inboundMessageCreated) {
    return new MarkMessageAsSeen(inboundMessageCreated.to(), inboundMessageCreated.from());
  }

  private CreateInboundMessage createInboundMessage(OutboundMessageCreated messageSent) {
    return new CreateInboundMessage(messageSent.to(), messageSent.from(), messageSent.message());
  }
}
