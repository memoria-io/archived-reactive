package io.memoria.jutils.core.eventsourcing.domain;

import io.memoria.jutils.core.eventsourcing.cmd.Resolver;
import io.memoria.jutils.core.eventsourcing.domain.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.generator.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public record UserResolver(IdGenerator idGenerator) implements Resolver<User, UserCommand, UserEvent> {
  @Override
  public Try<List<UserEvent>> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof AddFriend cmd)
      return apply(user, cmd);
    if (userCommand instanceof SendMessage cmd)
      return apply(user, cmd);

    return Try.failure(new Exception("Unknown event"));
  }

  private Try<List<UserEvent>> apply(User user, AddFriend cmd) {
    if (user.canAddFriend(cmd.friendId()))
      return Try.success(List.of(new FriendAdded(idGenerator.get(), user.id(), cmd.friendId())));
    else
      return Try.failure(ALREADY_EXISTS);
  }

  private Try<List<UserEvent>> apply(User user, SendMessage cmd) {
    if (user.canSendMessageTo(cmd.toFriendId()))
      return Try.success(List.of(new MessageSent(idGenerator.get(),
                                                 user.id(),
                                                 new Message(idGenerator.get(),
                                                             user.id(),
                                                             cmd.toFriendId(),
                                                             cmd.messageBody()))));
    else
      return Try.failure(NOT_FOUND);
  }
}
