//package io.memoria.reactive.core.eventsourcing.saga;
//
//import io.memoria.reactive.core.eventsourcing.Command;
//import io.memoria.reactive.core.eventsourcing.Event;
//import io.memoria.reactive.core.eventsourcing.State;
//import io.memoria.reactive.core.eventsourcing.UserEvent;
//import io.memoria.reactive.core.eventsourcing.UserEvent.MessageReceived;
//import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
//import io.memoria.reactive.core.eventsourcing.UserEvent.NotificationReceived;
//import io.memoria.reactive.core.eventsourcing.saga.SagaDecider;
//import io.vavr.control.Option;
//import io.vavr.control.Try;
//
//public record UserSagaDecider() implements SagaDecider {
//
//  @Override
//  public Try<Command> apply(State state, Event event) {
//    if (event instanceof UserEvent userEvent) {
//      return switch (userEvent) {
//        case MessageSent messageSent -> Option.some(toMessageReceived(messageSent));
//        case MessageReceived messageReceived -> Option.some(toNotification(messageReceived));
//        default -> Option.none();
//      };
//    } else
//      return Option.none();
//  }
//
//  private NotificationReceived toNotification(MessageReceived messageReceived) {
//    return new NotificationReceived(messageReceived.to());
//  }
//
//  private MessageReceived toMessageReceived(MessageSent messageSent) {
//    return new MessageReceived(messageSent.from(), messageSent.to(), messageSent.message());
//  }
//}
