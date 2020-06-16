package io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event;

import io.memoria.jutils.eventsourcing.event.EventHandler;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.Message;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.User;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class UserEventHandler implements EventHandler<User, UserEvent> {

  @Override
  public User apply(User user, UserEvent userEvent) {
    return Match(userEvent).of(Case($(instanceOf(UserEvent.MessageCreated.class)), ev -> createMessage(user, ev)),
                               Case($(instanceOf(UserEvent.FriendAdded.class)), ev -> addFriend(user, ev)),
                               Case($(instanceOf(UserEvent.MessageSeen.class)), ev -> messageSeen(user, ev)));
  }

  private User addFriend(User u, UserEvent.FriendAdded friendAdded) {
    return u.withNewFriend(friendAdded.friendId());
  }

  private User createMessage(User u, UserEvent.MessageCreated msg) {
    return u.withNewMessage(new Message(msg.messageId(), msg.from(), msg.to(), msg.body()));
  }

  private User messageSeen(User u, UserEvent.MessageSeen messageSeen) {
    return u.withMessageSeen(messageSeen.messageId(), true);
  }
}

