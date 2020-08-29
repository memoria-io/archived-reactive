package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Evolver;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;

public record UserEvolver() implements Evolver<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    if (userEvent instanceof AccountCreated e)
      return new Account(e.accountId(), e.age());
    if (user instanceof Account account && userEvent instanceof FriendAdded e)
      return account.withNewFriend(e.friendId());
    if (user instanceof Account account && userEvent instanceof MessageSent e)
      return account.withNewMessage(e.message().id());
    return user;
  }
}
