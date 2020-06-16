package io.memoria.jutils.eventsourcing.socialnetwork.domain.user.cmd;

import io.memoria.jutils.eventsourcing.cmd.Command;

public interface UserCommand extends Command {

  record AddFriend(String userId, String friendId) implements UserCommand {

  }

  record SendMessage(String fromUserId, String toUserId, String message) implements UserCommand{
}
}
