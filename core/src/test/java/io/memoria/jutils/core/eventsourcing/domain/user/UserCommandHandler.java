package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageSent;
import io.memoria.jutils.core.generator.IdGenerator;
import reactor.core.publisher.Flux;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public record UserCommandHandler(IdGenerator idGenerator) implements CommandHandler<User, UserCommand, UserEvent> {
  @Override
  public Flux<UserEvent> apply(User user, UserCommand userCommand) {
    if (user instanceof OnlineUser onlineUser) {
      if (userCommand instanceof AddFriend addFriend)
        return addFriend(addFriend, onlineUser);
      if (userCommand instanceof SendMessage sendMessage)
        return sendMessage(sendMessage, onlineUser, idGenerator.get());
    }
    return Flux.error(new Exception("Unknown event"));
  }

  private Flux<UserEvent> addFriend(AddFriend addFriend, OnlineUser onlineUser) {
    if (onlineUser.canAddFriend(addFriend.friendId()))
      return Flux.just(new FriendAdded(addFriend.aggId(), addFriend.friendId()));
    else
      return Flux.error(ALREADY_EXISTS);
  }

  private Flux<UserEvent> sendMessage(SendMessage sendMessage, OnlineUser onlineUser, String messageId) {
    if (onlineUser.canSendMessageTo(sendMessage.toFriendId()))
      return Flux.just(new MessageSent(sendMessage.aggId(),
                                       messageId,
                                       sendMessage.toFriendId(),
                                       sendMessage.message()));
    else
      return Flux.error(NOT_FOUND);
  }
}
