package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.domain.port.EventHandler;
import com.marmoush.jutils.eventsourcing.domain.value.Event;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.*;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserEventsTest {
  private EventHandler<User, UserEvent> userEventHandler = new UserEventHandler();

  @Test
  void userEvents() {
  }

  private User evolve(User user, List<Event> e) {
    return e.foldLeft(user,
                      (u, event) -> Match(event).of(Case($(instanceOf(UserEvent.class)), userEventHandler.apply(u))));
  }
}
