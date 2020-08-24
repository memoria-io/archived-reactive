package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Evolver;

public record UserEvolver() implements Evolver<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    if (userEvent instanceof UserEvent.AccountCreated e)
      return new User.Account(e.aggId(), e.age());
    if (user instanceof User.Account account && userEvent instanceof UserEvent.FriendAdded e)
      return account.withNewFriend(e.friendId());
    if (user instanceof User.Account account && userEvent instanceof UserEvent.MessageSent e)
      return account.withNewMessage(e.message().id());
    return user;
  }
}
