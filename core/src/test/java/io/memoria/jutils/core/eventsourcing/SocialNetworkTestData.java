package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
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
import io.memoria.jutils.core.eventsourcing.state.InMemoryStateStore;
import io.memoria.jutils.core.eventsourcing.state.StateStore;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
  public final Map<User, UserCommand> db;
  public final StateStore<User> stateStore;
  public final CommandHandler<User, UserCommand> handler;

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
    db = new HashMap<>();
    stateStore = new InMemoryStateStore<>(new HashMap<>());
    handler = new CommandHandler<>(stateStore,
                                   new UserEvolver(),
                                   new UserDecider(eventsIdGen),
                                   new Visitor(new Id("0")));
  }
}
