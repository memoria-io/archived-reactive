package io.memoria.jutils.core.eventsourcing.domain.user.cmd;

import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.cmd.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.event.UserEvent.MessageCreated;
import io.memoria.jutils.core.eventsourcing.event.Event;
import reactor.core.publisher.Flux;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public record UserCommandHandler() implements CommandHandler<User, UserCommand, Event> {

  @Override
  public Flux<Event> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof SendMessage cmd)
      return sendMessage(user, cmd);
    if (userCommand instanceof AddFriend cmd)
      return addFriend(user, cmd);
    return Flux.error(new Exception("Unknown command"));
  }

  private Flux<Event> addFriend(User user, AddFriend m) {
    if (user.canAddFriend(m.friendId())) {
      return Flux.just(new FriendAdded(m.userId(), m.friendId()));
    } else {
      return Flux.error(ALREADY_EXISTS);
    }
  }

  private Flux<Event> sendMessage(User user, SendMessage m) {
    if (user.canSendMessageTo(m.toUserId())) {
      var created = new MessageCreated("messageId", m.fromUserId(), m.toUserId(), m.message());
      return Flux.just(created);
    } else {
      return Flux.error(NOT_FOUND);
    }
  }
}
