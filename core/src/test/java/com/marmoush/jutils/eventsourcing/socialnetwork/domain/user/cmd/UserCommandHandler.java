package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.cmd;

import com.marmoush.jutils.eventsourcing.cmd.CommandHandler;
import com.marmoush.jutils.eventsourcing.event.Event;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.User;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event.*;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static com.marmoush.jutils.core.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.core.domain.error.NotFound.NOT_FOUND;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class UserCommandHandler implements CommandHandler<User, UserCommand, Event> {

  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    return Match(userCommand).of(API.Case(API.$(instanceOf(SendMessage.class)), c -> sendMessage(user, c)),
                                 API.Case(API.$(instanceOf(AddFriend.class)), c -> addFriend(user, c)));
  }

  private static Try<List<Event>> sendMessage(User user, SendMessage m) {
    return validateSendMessage(user, m).map(v -> {
      var created = new MessageCreated("messageId", m.fromUserId, m.toUserId, m.message);
      return List.of(created);
    });
  }

  private static Try<List<Event>> addFriend(User user, AddFriend m) {
    return validateAddFriend(user, m).map(v -> List.of(new FriendAdded(m.userId, m.friendId)));
  }

  private static Try<Void> validateSendMessage(User user, SendMessage m) {
    return (user.friends.contains(m.toUserId)) ? Try.success(null) : Try.failure(NOT_FOUND);
  }

  private static Try<Void> validateAddFriend(User user, AddFriend m) {
    return (user.friends.contains(m.friendId)) ? Try.failure(ALREADY_EXISTS) : Try.success(null);
  }
}
