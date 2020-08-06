package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.cmd.Command;

public interface UserCommand extends Command {
  record AddFriend(String aggId, String friendId) implements UserCommand {}

  record SendMessage(String aggId, String toFriendId, String message) implements UserCommand {}
}
