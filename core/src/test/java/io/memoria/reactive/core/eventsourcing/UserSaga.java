package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.UserEvent.MessageReceived;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.UserEvent.NotificationReceived;
import io.vavr.control.Option;

public record UserSaga() implements Saga {

  @Override
  public Option<Event> apply(Event event) {
    if (event instanceof UserEvent userEvent) {
      return switch (userEvent) {
        case MessageSent messageSent -> Option.some(toMessageReceived(messageSent));
        case MessageReceived messageReceived -> Option.some(toNotification(messageReceived));
        default -> Option.none();
      };
    } else
      return Option.none();
  }

  private NotificationReceived toNotification(MessageReceived messageReceived) {
    return new NotificationReceived(messageReceived.to());
  }

  private MessageReceived toMessageReceived(MessageSent messageSent) {
    return new MessageReceived(messageSent.from(), messageSent.to(), messageSent.message());
  }
}
