package io.memoria.jutils.core.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserDecider;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.eventsourcing.stateful.StatefulCommandHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialNetworkTestData {
  private static final Random random = new Random();
  private final AtomicInteger atomic = new AtomicInteger();
  public final IdGenerator idGenerator = () -> Id.of(atomic.getAndIncrement() + "");
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
  // Command Handler 
  public final CommandHandler<UserCommand> handler;

  public SocialNetworkTestData() {
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
    // Command handlers
    handler = new StatefulCommandHandler<>(new Visitor(), new UserEvolver(), new UserDecider(idGenerator));
  }
}
