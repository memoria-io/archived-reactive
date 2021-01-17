package io.memoria.jutils.r2es.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.r2es.socialnetwork.domain.User.Account;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.MessageSent;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (event instanceof AccountCreated e)
      return new Account(e.age());
    if (user instanceof Account account && event instanceof FriendAdded e)
      return account.withNewFriend(e.friendId());
    if (user instanceof Account account && event instanceof MessageSent e)
      return account.withNewMessage(e.message().id());
    return user;
  }
}
