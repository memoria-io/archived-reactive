package io.memoria.jutils.jcore.eventsourcing.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.Evolver;
import io.memoria.jutils.jcore.eventsourcing.State;
import io.memoria.jutils.jcore.eventsourcing.user.User.Account;
import io.memoria.jutils.jcore.eventsourcing.user.User.Visitor;
import io.memoria.jutils.jcore.eventsourcing.user.UserEvent.MessageSent;

public record UserEvolver() implements Evolver {
  @Override
  public State apply(State user, Event event) {
    if (user instanceof Visitor && event instanceof UserEvent.UserCreated e) {
      return new Account(e.name());
    }
    if (user instanceof Account acc && event instanceof MessageSent e) {
      return acc.withMessage(e.receiverId(), e.message());
    }
    return user;
  }
}
