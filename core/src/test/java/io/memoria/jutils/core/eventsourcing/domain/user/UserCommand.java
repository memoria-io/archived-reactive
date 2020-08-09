package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.cmd.Command;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageSent;
import io.memoria.jutils.core.generator.IdGenerator;
import reactor.core.publisher.Flux;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public interface UserCommand extends Command {
  record AddFriend(String friendId) implements UserCommand {
    Flux<UserEvent> apply(User user, IdGenerator idGenerator) {
      if (user.canAddFriend(friendId))
        return Flux.just(new FriendAdded(idGenerator.get(), user.id(), friendId()));
      else
        return Flux.error(ALREADY_EXISTS);
    }
  }

  record SendMessage(String toFriendId, String messageBody) implements UserCommand {
    Flux<UserEvent> apply(User user, IdGenerator idGenerator) {
      if (user.canSendMessageTo(toFriendId))
        return Flux.just(new MessageSent(idGenerator.get(),
                                         user.id(),
                                         new Message(idGenerator.get(), user.id(), toFriendId, messageBody)));
      else
        return Flux.error(NOT_FOUND);
    }
  }
}
