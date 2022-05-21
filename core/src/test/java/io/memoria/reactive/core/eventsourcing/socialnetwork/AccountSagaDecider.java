package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaDecider;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateInboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateNewMsgNotification;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.OutboundMsgCreated;
import io.vavr.control.Option;

record AccountSagaDecider() implements SagaDecider {

  @Override
  public Option<Command> apply(Event event) {
    return event instanceof AccountEvent accountEvent ? handleUserEvent(accountEvent) : Option.none();
  }

  private Option<Command> handleUserEvent(AccountEvent accountEvent) {
    return switch (accountEvent) {
      case OutboundMsgCreated messageSent -> Option.some(createInboundMessage(messageSent));
      case InboundMsgCreated inboundMsgCreated -> Option.some(createNewMsgNotification(inboundMsgCreated));
      case default -> Option.none();
    };
  }

  private CreateInboundMsg createInboundMessage(OutboundMsgCreated messageSent) {
    return AccountCommand.createInboundMsg(messageSent.msgSender(), messageSent.msgReceiver(), messageSent.message());
  }

  private CreateNewMsgNotification createNewMsgNotification(InboundMsgCreated inboundMsgCreated) {
    return AccountCommand.createNewMsgNotification(inboundMsgCreated.msgReceiver());
  }
}
