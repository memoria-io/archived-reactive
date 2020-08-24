package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.cmd.Command;

public interface UserCommand extends Command {
  record AddFriend(String userId, String friendId) implements UserCommand {}

  record CreateAccount(String id, int age) implements UserCommand {}

  record SendMessage(String userId, String toFriendId, String messageBody) implements UserCommand {}
}
