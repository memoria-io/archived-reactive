package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.UserCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.UserCommand.MarkMsgAsSeen;
import io.memoria.reactive.core.eventsourcing.UserEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.saga.SagaDecider;
import io.vavr.control.Option;

public record UserSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return event instanceof UserEvent userEvent ? handleUserEvent(userEvent) : Option.none();
  }

  private CreateInboundMsg createInboundMessage(OutboundMsgCreated messageSent) {
    return new CreateInboundMsg(messageSent.to(), messageSent.userId(), messageSent.message());
  }

  private Option<Command> handleUserEvent(UserEvent userEvent) {
    return switch (userEvent) {
      case OutboundMsgCreated messageSent -> Option.some(createInboundMessage(messageSent));
      case InboundMsgCreated inboundMsgCreated -> Option.some(markMessageAsSeen(inboundMsgCreated));
      default -> Option.none();
    };
  }

  private MarkMsgAsSeen markMessageAsSeen(InboundMsgCreated inboundMsgCreated) {
    return new MarkMsgAsSeen(inboundMsgCreated.from(), inboundMsgCreated.userId());
  }
}
