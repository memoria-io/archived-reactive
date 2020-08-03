package io.memoria.jutils.core.eventsourcing.domain.user.cmd;

import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.domain.user.User;

public interface UserCommand extends Command<User> {

  record AddFriend(String userId, String friendId) implements UserCommand {}

  record SendMessage(String fromUserId, String toUserId, String message) implements UserCommand {}
}
