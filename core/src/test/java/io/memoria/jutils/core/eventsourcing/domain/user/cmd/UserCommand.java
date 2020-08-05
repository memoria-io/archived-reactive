package io.memoria.jutils.core.eventsourcing.domain.user.cmd;

import io.memoria.jutils.core.eventsourcing.User;
import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.domain.user.OnlineUser;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public interface UserCommand extends Command<User, UserEvent> {

  record AddFriend(String userId, String friendId) implements UserCommand {
    @Override
    public Try<List<UserEvent>> apply(User user) {
      if (user instanceof OnlineUser onlineUser && onlineUser.canAddFriend(friendId())) {
        return Try.success(List.of(new FriendAdded(userId(), friendId())));
      } else {
        return Try.failure(ALREADY_EXISTS);
      }
    }
  }

  record SendMessage(String fromUserId, String toUserId, String messageId, String message) implements UserCommand {
    @Override
    public Try<List<UserEvent>> apply(User user) {
      if (user instanceof OnlineUser onlineUser && onlineUser.canSendMessageTo(toUserId())) {
        var created = new MessageCreated(messageId(), fromUserId(), toUserId(), message());
        return Try.success(List.of(created));
      } else {
        return Try.failure(NOT_FOUND);
      }
    }
  }
}
