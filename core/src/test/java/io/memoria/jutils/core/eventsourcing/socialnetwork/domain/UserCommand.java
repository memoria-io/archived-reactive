package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.value.Id;

public interface UserCommand extends Command {
  record AddFriend(Id commandId, Id aggId, Id friendId) implements UserCommand {}

  record CreateAccount(Id commandId, Id aggId, int age) implements UserCommand {}

  record SendMessage(Id commandId, Id aggId, Id toFriendId, String messageBody) implements UserCommand {}
}
