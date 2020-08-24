package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.cmd.Decider;
import io.memoria.jutils.core.generator.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;

public record UserDecider(IdGenerator idGenerator) implements Decider<User, UserCommand, UserEvent> {

  @Override
  public Try<List<UserEvent>> apply(User user, UserCommand userCommand) {
    if (userCommand instanceof UserCommand.CreateAccount cmd)
      return apply(cmd);
    if (user instanceof User.Account account && userCommand instanceof UserCommand.AddFriend cmd)
      return apply(account, cmd);
    if (user instanceof User.Account account && userCommand instanceof UserCommand.SendMessage cmd)
      return apply(account, cmd);
    return Try.failure(new Exception("Unknown event"));
  }

  private Try<List<UserEvent>> apply(UserCommand.CreateAccount cmd) {
    return Try.success(List.of(new UserEvent.AccountCreated(idGenerator.get(), cmd.id(), cmd.age())));
  }

  private Try<List<UserEvent>> apply(User.Account account, UserCommand.AddFriend cmd) {
    if (account.canAddFriend(cmd.friendId()))
      return Try.success(List.of(new UserEvent.FriendAdded(idGenerator.get(), account.id(), cmd.friendId())));
    else
      return Try.failure(ALREADY_EXISTS);
  }

  private Try<List<UserEvent>> apply(User.Account user, UserCommand.SendMessage cmd) {
    if (user.canSendMessageTo(cmd.toFriendId())) {
      var msg = new Message(idGenerator.get(), user.id(), cmd.toFriendId(), cmd.messageBody());
      return Try.success(List.of(new UserEvent.MessageSent(idGenerator.get(), user.id(), msg)));
    } else
      return Try.failure(NOT_FOUND);
  }
}
