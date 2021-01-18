package io.memoria.jutils.r2es.socialnetwork.domain;

import io.memoria.jutils.core.JutilsException;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.ESException;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.r2es.socialnetwork.domain.User.Account;
import io.memoria.jutils.r2es.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.MessageSent;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.JutilsException.alreadyExists;

public record UserDecider(IdGenerator idGenerator) implements Decider<User, UserCommand> {
  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    if (user instanceof Visitor && userCommand instanceof CreateAccount cmd) {
      return Try.success(List.of(new AccountCreated(idGenerator.get(), cmd.userId(), cmd.age())));
    }
    if (user instanceof Account account && userCommand instanceof AddFriend cmd) {
      if (account.canAddFriend(cmd.friendId()))
        return Try.success(List.of(new FriendAdded(idGenerator.get(), cmd.userId(), cmd.friendId())));
      else
        return Try.failure(alreadyExists("Account already exists"));
    }
    if (user instanceof Account account && userCommand instanceof SendMessage cmd) {
      if (account.canSendMessageTo(cmd.toFriendId())) {
        var msg = new Message(idGenerator.get(), cmd.userId(), cmd.toFriendId(), cmd.messageBody());
        return Try.success(List.of(new MessageSent(idGenerator.get(), cmd.userId(), msg)));
      } else
        return Try.failure(JutilsException.notFound("Friend not found"));
    }
    return Try.failure(ESException.invalidOperation(user.getClass().getSimpleName(),
                                                    UserCommand.class.getSimpleName()));
  }
}