package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.CommandHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.msg.MessageEvent.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.AddFriend;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserCommand.SendMessage;
import com.marmoush.jutils.general.domain.port.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static com.marmoush.jutils.general.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.general.domain.error.NotFound.NOT_FOUND;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserCommandHandler implements CommandHandler<User, UserCommand, UserEvent> {
  private final IdGenerator idGenerator;

  public UserCommandHandler(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Try<List<UserEvent>> apply(User user, UserCommand userCommand) {
    return Match(userCommand).of(Case($(instanceOf(SendMessage.class)), c -> sendMessage(user, c)),
                                 Case($(instanceOf(AddFriend.class)), c -> addFriend(user, c)));
  }

  private Try<List<UserEvent>> sendMessage(User user, SendMessage m) {
    var r = (user.friends.contains(m.toUserId)) ? Try.<Void>success(null) : Try.<Void>failure(NOT_FOUND);
    return r.map(v -> {
      var id = idGenerator.generate();
      var created = new MessageCreated(m.flowId, id, m.fromUserId, m.toUserId, m.message);
      var sent = new MessageSent(m.flowId, id, m.fromUserId);
      var received = new MessageReceived(m.flowId, id, m.toUserId);
      return List.of(created, sent, received);
    });
  }

  private Try<List<UserEvent>> addFriend(User user, AddFriend m) {
    var r = (user.friends.contains(m.friendId)) ? Try.<Void>failure(ALREADY_EXISTS) : Try.<Void>success(null);
    return r.map(v -> List.of(new FriendAdded(m.flowId, m.userId, m.friendId)));
  }
}
