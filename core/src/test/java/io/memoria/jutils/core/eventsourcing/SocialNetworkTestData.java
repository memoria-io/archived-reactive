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
import io.memoria.jutils.core.eventsourcing.stateful.StatefulCommandHandler;
import io.memoria.jutils.core.eventsourcing.stateless.SqlCommandHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.Random;

public class SocialNetworkTestData {
  public final IdGenerator eventsIdGen;
  public final Id userId;
  public final Id friendId;
  public final Id topic;
  // State
  public final User visitor;
  // Commands
  public final UserCommand create;
  public final UserCommand add;
  public final UserCommand send;
  // Events
  public final UserEvent accountCreated;
  public final UserEvent friendAdded;
  public final UserEvent messageSent;
  // Command Handler 
  public final SqlCommandHandler<User, UserCommand> sqlHandler;
  public final StatefulCommandHandler<User, UserCommand> statefulHandler;

  public SocialNetworkTestData() {
    eventsIdGen = () -> new Id("event_0");
    userId = new Id("alex_" + new Random().nextInt(1000));
    friendId = new Id("bob_" + new Random().nextInt(1000));
    topic = userId;
    // State
    visitor = new Visitor(userId);
    // Commands
    create = new CreateAccount(userId, 18);
    add = new AddFriend(userId, friendId);
    send = new SendMessage(userId, friendId, "hello");
    // Events
    friendAdded = new FriendAdded(eventsIdGen.get(), friendId);
    accountCreated = new AccountCreated(eventsIdGen.get(), userId, 18);
    messageSent = new MessageSent(eventsIdGen.get(), new Message(eventsIdGen.get(), userId, friendId, "hello"));
    // Command handlers
    sqlHandler = getSqlHandler();
    statefulHandler = getStatefulHandler();
  }

  private StatefulCommandHandler<User, UserCommand> getStatefulHandler() {
    return new StatefulCommandHandler<>(visitor, new UserEvolver(), new UserDecider(eventsIdGen));
  }

  private SqlCommandHandler<User, UserCommand> getSqlHandler() {
    return null;
    //    return new SqlCommandHandler<User, UserCommand>(visitor,
    //                                                    new UserDecider(eventsIdGen),
    //                                                    new Visitor(new Id("0")));
  }
}
