package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.CommandHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.AddFriend;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.SendMessage;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserEvent.FriendAdded;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserEvent.MessageCreated;
import com.marmoush.jutils.general.domain.port.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static com.marmoush.jutils.general.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.general.domain.error.NotFound.NOT_FOUND;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserCommandHandler implements CommandHandler<User, UserCommand, Event> {

  @Override
  public Try<List<Event>> apply(User user, UserCommand userCommand) {
    return Match(userCommand).of(Case($(instanceOf(SendMessage.class)), c -> sendMessage(user, c)),
                                 Case($(instanceOf(AddFriend.class)), c -> addFriend(user, c)));
  }

  private Try<List<Event>> sendMessage(User user, SendMessage m) {
    var r = (user.friends.contains(m.toUserId)) ? Try.<Void>success(null) : Try.<Void>failure(NOT_FOUND);
    return r.map(v -> {
      var created = new MessageCreated(m.flowId, m.fromUserId, m.toUserId, m.message);
      return List.of(created);
    });
  }

  private Try<List<Event>> addFriend(User user, AddFriend m) {
    var r = (user.friends.contains(m.friendId)) ? Try.<Void>failure(ALREADY_EXISTS) : Try.<Void>success(null);
    return r.map(v -> List.of(new FriendAdded(m.flowId, m.userId, m.friendId)));
  }
}
