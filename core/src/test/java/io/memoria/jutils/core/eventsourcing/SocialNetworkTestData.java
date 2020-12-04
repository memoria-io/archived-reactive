package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.User;
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
import io.memoria.jutils.core.eventsourcing.stateful.InMemoryStateStore;
import io.memoria.jutils.core.eventsourcing.stateful.StateStore;
import io.memoria.jutils.core.eventsourcing.stateful.StatefulHandler;
import io.memoria.jutils.core.eventsourcing.stateless.InMemoryCommandHandler;
import io.memoria.jutils.core.eventsourcing.stateless.StatelessHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class SocialNetworkTestData {
  public final IdGenerator eventsIdGen;
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
  public final StatelessHandler<User, UserCommand> handler;
  // Blocking Command Handler
  public final Map<User, UserCommand> db;
  public final StateStore<User> blockingStateStore;
  public final StatefulHandler<User, UserCommand> blockingHandler;

  public SocialNetworkTestData() {
    eventsIdGen = () -> new Id("event_0");
    userId = new Id("alex_" + new Random().nextInt(1000));
    friendId = new Id("bob_" + new Random().nextInt(1000));
    topic = userId;

    create = new CreateAccount(userId, 18);
    add = new AddFriend(userId, friendId);
    send = new SendMessage(userId, friendId, "hello");
    // Events
    friendAdded = new FriendAdded(eventsIdGen.get(), friendId);
    accountCreated = new AccountCreated(eventsIdGen.get(), userId, 18);
    messageSent = new MessageSent(eventsIdGen.get(), new Message(eventsIdGen.get(), userId, friendId, "hello"));
    // Handler
    var eventStore = new InMemoryCommandHandler(new HashMap<>());
    handler = new StatelessHandler<>(eventStore,
                                     new UserEvolver(),
                                     new UserDecider(eventsIdGen),
                                     new Visitor(new Id("0")));
    // Blocking Handler
    db = new HashMap<>();
    blockingStateStore = new InMemoryStateStore<>(new ConcurrentHashMap<>());
    blockingHandler = new StatefulHandler<>(blockingStateStore,
                                            new UserEvolver(),
                                            new UserDecider(eventsIdGen),
                                            new Visitor(new Id("0")));
  }
}