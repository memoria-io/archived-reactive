package io.memoria.jutils.eventsourcing.socialnetwork.domain.user.cmd;

import io.memoria.jutils.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.eventsourcing.event.Event;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.User;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.UserEvent.FriendAdded;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event.UserEvent.MessageCreated;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static io.memoria.jutils.core.domain.Err.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.Err.NotFound.NOT_FOUND;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class UserCommandHandler implements CommandHandler<User, UserCommand, Event> {

  private static Try<List<Event>> addFriend(User user, UserCommand.AddFriend m) {
    return validateAddFriend(user, m).map(v -> List.of(new FriendAdded(m.userId(), m.friendId())));
  }

  private static Try<List<Event>> sendMessage(User user, UserCommand.SendMessage m) {
    return validateSendMessage(user, m).map(v -> {
      var created = new MessageCreated("messageId", m.fromUserId(), m.toUserId(), m.message());
      return List.of(created);
    });
  }

  private static Try<Void> validateAddFriend(User user, UserCommand.AddFriend m) {
    return (user.friends().contains(m.friendId())) ? Try.failure(ALREADY_EXISTS) : Try.success(null);
  }

  private static Try<Void> validateSendMessage(User user, UserCommand.SendMessage m) {
    return (user.friends().contains(m.toUserId())) ? Try.success(null) : Try.failure(NOT_FOUND);
  }

  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    return Match(userCommand).of(API.Case(API.$(instanceOf(UserCommand.SendMessage.class)), c -> sendMessage(user, c)),
                                 API.Case(API.$(instanceOf(UserCommand.AddFriend.class)), c -> addFriend(user, c)));
  }
}
