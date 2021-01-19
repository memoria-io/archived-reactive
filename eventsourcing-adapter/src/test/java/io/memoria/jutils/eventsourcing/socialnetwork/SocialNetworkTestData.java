package io.memoria.jutils.eventsourcing.socialnetwork;

import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.core.id.IdGenerator;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.Message;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvent;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;

import java.util.Random;

public class SocialNetworkTestData {
  public final Id userId;
  public final Id friendId;
  public final Id topic;
  // Commands
  public final UserCommand create;
  public final UserCommand add;
  public final UserCommand send;
  // Events
  public final UserEvent accountCreated;
  public final UserEvent friendAdded;
  public final UserEvent messageSent;

  public SocialNetworkTestData(Random random, IdGenerator idGenerator) {
    userId = Id.of("alex_" + random.nextInt(10000));
    friendId = Id.of("bob_" + random.nextInt(10000));
    topic = userId;
    // State
    // Commands
    create = new CreateAccount(idGenerator.get(), userId, 18);
    add = new AddFriend(idGenerator.get(), userId, friendId);
    send = new SendMessage(idGenerator.get(), userId, friendId, "hello");
    // Events
    accountCreated = new AccountCreated(Id.of(3), userId, 18);
    friendAdded = new FriendAdded(Id.of(4), userId, friendId);
    messageSent = new MessageSent(Id.of(6), userId, new Message(Id.of(5), userId, friendId, "hello"));
  }
}
