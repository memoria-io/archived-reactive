package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaDecider;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateNewMsgNotification;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.OutboundMsgCreated;
import io.vavr.control.Option;

record UserSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return event instanceof UserEvent userEvent ? handleUserEvent(userEvent) : Option.none();
  }

  private Option<Command> handleUserEvent(UserEvent userEvent) {
    return switch (userEvent) {
      case OutboundMsgCreated messageSent -> Option.some(createInboundMessage(messageSent));
      case InboundMsgCreated inboundMsgCreated -> Option.some(createNewMsgNotification(inboundMsgCreated));
      case default -> Option.none();
    };
  }

  private CreateInboundMsg createInboundMessage(OutboundMsgCreated messageSent) {
    return UserCommand.createInboundMsg(messageSent.msgSender(), messageSent.msgReceiver(), messageSent.message());
  }

  private CreateNewMsgNotification createNewMsgNotification(InboundMsgCreated inboundMsgCreated) {
    return UserCommand.createNewMsgNotification(inboundMsgCreated.msgReceiver());
  }
}
