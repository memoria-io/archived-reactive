package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserValue.Account;

public record UserEvolver() implements Evolver<UserValue> {
  @Override
  public UserValue apply(UserValue user, Event event) {
    if (event instanceof AccountCreated e)
      return new Account(e.age());
    if (user instanceof Account account && event instanceof FriendAdded e)
      return account.withNewFriend(e.friendId());
    if (user instanceof Account account && event instanceof MessageSent e)
      return account.withNewMessage(e.message().id());
    return user;
  }
}
