package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user;

import com.marmoush.jutils.eventsourcing.domain.port.CommandHandler;
import com.marmoush.jutils.eventsourcing.domain.value.Event;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static com.marmoush.jutils.general.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.general.domain.error.NotFound.NOT_FOUND;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

public class UserCommandHandler implements CommandHandler<User, UserCommand, Event> {

  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    return Match(userCommand).of(API.Case(API.$(instanceOf(UserCommand.SendMessage.class)), c -> sendMessage(user, c)),
                                 API.Case(API.$(instanceOf(UserCommand.AddFriend.class)), c -> addFriend(user, c)));
  }

  // TODO validation and add new errors (eg. already_friend, invalidArguments)
  private Try<List<Event>> sendMessage(User user, UserCommand.SendMessage m) {
    var r = (user.friends.contains(m.toUserId)) ? Try.<Void>success(null) : Try.<Void>failure(NOT_FOUND);
    return r.map(v -> {
      var created = new UserEvent.MessageCreated(m.fromUserId, m.toUserId, m.message);
      return List.of(created);
    });
  }

  private Try<List<Event>> addFriend(User user, UserCommand.AddFriend m) {
    var r = (user.friends.contains(m.friendId)) ? Try.<Void>failure(ALREADY_EXISTS) : Try.<Void>success(null);
    return r.map(v -> List.of(new UserEvent.FriendAdded(m.userId, m.friendId)));
  }
}
