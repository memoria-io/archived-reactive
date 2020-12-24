package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.value.Id;

public interface UserCommand extends Command {
  record AddFriend(Id commandId, Id userId, Id friendId) implements UserCommand {}

  record CreateAccount(Id commandId, Id userId, int age) implements UserCommand {}

  record SendMessage(Id commandId, Id userId, Id toFriendId, String messageBody) implements UserCommand {}

  @Override
  default Id aggId() {
    return userId();
  }

  Id userId();
}
