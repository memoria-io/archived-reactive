package io.memoria.jutils.core.eventsourcing.domain.user.cmd;

import io.memoria.jutils.core.eventsourcing.cmd.Command;

public interface UserCommand extends Command {

  record AddFriend(String userId, String friendId) implements UserCommand {

  }

  record SendMessage(String fromUserId, String toUserId, String message) implements UserCommand {}
}
