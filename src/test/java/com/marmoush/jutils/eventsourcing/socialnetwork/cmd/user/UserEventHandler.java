package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.EventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserEvent.FriendAdded;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserEventHandler implements EventHandler<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    return Match(userEvent).of(Case($(instanceOf(FriendAdded.class)), ev -> addFriend(user, ev)));
  }

  private User addFriend(User u, FriendAdded friendAdded) {
    return u.withNewFriend(friendAdded.friendId);
  }
}
