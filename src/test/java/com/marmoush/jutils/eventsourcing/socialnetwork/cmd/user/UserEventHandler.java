package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.EventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserEvent.FriendAdded;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.UserEvent.MessageCreated;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Message;
import com.marmoush.jutils.general.domain.port.IdGenerator;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserEventHandler implements EventHandler<User, UserEvent> {
  private final IdGenerator id;

  public UserEventHandler(IdGenerator id) {
    this.id = id;
  }

  @Override
  public User apply(User user, UserEvent userEvent) {
    return Match(userEvent).of(Case($(instanceOf(MessageCreated.class)), ev -> createMessage(user, ev)),
                               Case($(instanceOf(FriendAdded.class)), ev -> addFriend(user, ev)));
  }

  private User addFriend(User u, FriendAdded friendAdded) {
    return u.withNewFriend(friendAdded.friendId);
  }

  private User createMessage(User u, MessageCreated msg) {
    var m = new Message(msg.from, msg.to, msg.body, false);
    return u.withNewMessage(m);
  }
}
