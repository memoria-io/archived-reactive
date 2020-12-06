package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.generator.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;
import static io.memoria.jutils.core.eventsourcing.ESException.invalidOperation;

public record UserDecider(IdGenerator idGenerator) implements Decider<User, UserCommand> {

  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    if (user instanceof User.Visitor && userCommand instanceof CreateAccount cmd)
      return apply(cmd);
    if (user instanceof Account account && userCommand instanceof AddFriend cmd)
      return apply(account, cmd);
    if (user instanceof Account account && userCommand instanceof SendMessage cmd)
      return apply(account, cmd);
    return Try.failure(invalidOperation(user, userCommand));
  }

  private Try<List<Event>> apply(CreateAccount cmd) {
    return Try.success(List.of(new AccountCreated(idGenerator.get(), cmd.aggId(), cmd.age())));
  }

  private Try<List<Event>> apply(Account account, AddFriend cmd) {
    if (account.canAddFriend(cmd.friendId()))
      return Try.success(List.of(new FriendAdded(idGenerator.get(), cmd.aggId(), cmd.friendId())));
    else
      return Try.failure(ALREADY_EXISTS);
  }

  private Try<List<Event>> apply(Account user, SendMessage cmd) {
    if (user.canSendMessageTo(cmd.toFriendId())) {
      var msg = new Message(idGenerator.get(), user.id(), cmd.toFriendId(), cmd.messageBody());
      return Try.success(List.of(new MessageSent(idGenerator.get(), cmd.aggId(), msg)));
    } else
      return Try.failure(NOT_FOUND);
  }
}
