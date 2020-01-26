package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user;

import com.marmoush.jutils.eventsourcing.domain.port.EventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.FriendAdded;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.MessageCreated;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.Message;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserEventHandler implements EventHandler<User, UserEvent> {

  @Override
  public User apply(User user, UserEvent userEvent) {
    return Match(userEvent).of(Case($(instanceOf(MessageCreated.class)), ev -> createMessage(user, ev)),
                               Case($(instanceOf(FriendAdded.class)), ev -> addFriend(user, ev)));
  }

  private User addFriend(User u, FriendAdded friendAdded) {
    return u.withNewFriend(friendAdded.friendId);
  }

  private User createMessage(User u, MessageCreated msg) {
    var m = new Message(msg.from, msg.to, msg.body, false);
    return u.withNewMessage(m);
  }
}
