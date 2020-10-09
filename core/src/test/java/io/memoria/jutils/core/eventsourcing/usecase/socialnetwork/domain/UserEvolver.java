package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.Evolver;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (event instanceof AccountCreated e)
      return new Account(e.accountId(), e.age());
    if (user instanceof Account account && event instanceof FriendAdded e)
      return account.withNewFriend(e.friendId());
    if (user instanceof Account account && event instanceof MessageSent e)
      return account.withNewMessage(e.message().id());
    return user;
  }
}
