package io.memoria.jutils.core.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.User;
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
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserValue;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserValue.Visitor;
import io.memoria.jutils.core.eventsourcing.socialnetwork.transformer.SocialNetworkTransformer;
import io.memoria.jutils.core.eventsourcing.stateful.StatefulCommandHandler;
import io.memoria.jutils.core.eventsourcing.stateless.SqlCommandHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;
import reactor.core.scheduler.Schedulers;

import javax.sql.PooledConnection;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialNetworkTestData {
  private static final Random random = new Random();
  private final AtomicInteger atomic = new AtomicInteger();
  public final IdGenerator idGenerator = () -> new Id(atomic.getAndIncrement() + "");
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
  public final CommandHandler<UserCommand> handler;

  public SocialNetworkTestData(Option<PooledConnection> pooledConnection) throws SQLException {
    userId = new Id("alex_" + random.nextInt(10000));
    friendId = new Id("bob_" + random.nextInt(10000));
    topic = userId;
    // State
    visitor = new User(userId, new Visitor());
    // Commands
    create = new CreateAccount(idGenerator.get(), userId, 18);
    add = new AddFriend(idGenerator.get(), userId, friendId);
    send = new SendMessage(idGenerator.get(), userId, friendId, "hello");
    // Events
    accountCreated = new AccountCreated(new Id("3"), userId, 18);
    friendAdded = new FriendAdded(new Id("4"), userId, friendId);
    messageSent = new MessageSent(new Id("6"), userId, new Message(new Id("5"), userId, friendId, "hello"));
    // Command handlers
    handler = (pooledConnection.isEmpty()) ? getStatefulHandler() : getSqlHandler(pooledConnection.get());
  }

  private SqlCommandHandler<UserValue, UserCommand> getSqlHandler(PooledConnection pooledConnection) {
    return new SqlCommandHandler<>(pooledConnection,
                                   new SocialNetworkTransformer(),
                                   new UserValue.Visitor(),
                                   new UserEvolver(),
                                   new UserDecider(idGenerator),
                                   Schedulers.boundedElastic());
  }

  private StatefulCommandHandler<UserValue, UserCommand> getStatefulHandler() {
    return new StatefulCommandHandler<>(new Visitor(), new UserEvolver(), new UserDecider(idGenerator));
  }
}
