package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageCreated;
import reactor.core.publisher.Flux;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public interface UserCommand extends Command {
  record AddFriend(String userId, String friendId) implements UserCommand {
    public Flux<UserEvent> apply(OnlineUser onlineUser) {
      if (onlineUser.canAddFriend(friendId()))
        return Flux.just(new FriendAdded(userId(), friendId()));
      else
        return Flux.error(ALREADY_EXISTS);
    }
  }

  record SendMessage(String userId, String friendId, String message) implements UserCommand {
    public Flux<UserEvent> apply(OnlineUser onlineUser, String messageId) {
      if (onlineUser.canSendMessageTo(friendId()))
        return Flux.just(new MessageCreated(messageId, userId(), friendId(), message()));
      else
        return Flux.error(NOT_FOUND);
    }
  }

  String userId();
}
