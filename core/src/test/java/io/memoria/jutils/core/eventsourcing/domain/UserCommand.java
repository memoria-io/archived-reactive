package io.memoria.jutils.core.eventsourcing.domain;

import io.memoria.jutils.core.eventsourcing.cmd.Command;

public interface UserCommand extends Command {
  record AddFriend(String friendId) implements UserCommand {}

  record SendMessage(String toFriendId, String messageBody) implements UserCommand {}
}
