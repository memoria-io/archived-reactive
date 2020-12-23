package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserValue.Account;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (event instanceof AccountCreated e)
      return new User(e.aggId(), new Account(e.age()));
    if (user.value instanceof Account account && event instanceof FriendAdded e)
      return new User(user.id, account.withNewFriend(e.friendId()));
    if (user.value instanceof Account account && event instanceof MessageSent e)
      return new User(user.id, account.withNewMessage(e.message().id()));
    return user;
  }
}
