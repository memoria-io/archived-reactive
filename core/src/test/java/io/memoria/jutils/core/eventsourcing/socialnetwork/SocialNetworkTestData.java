package io.memoria.jutils.core.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
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
import io.memoria.jutils.core.eventsourcing.socialnetwork.transformer.SocialNetworkTransformer;
import io.memoria.jutils.core.eventsourcing.stateful.StatefulCommandHandler;
import io.memoria.jutils.core.eventsourcing.stateless.SqlCommandHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;
import org.h2.jdbcx.JdbcDataSource;
import reactor.core.scheduler.Schedulers;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SocialNetworkTestData {
  private final AtomicInteger atomic = new AtomicInteger();
  public final IdGenerator eventsIdGen = () -> new Id(atomic.getAndIncrement() + "");
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
  public final CommandHandler<User, UserCommand> handler;

  public SocialNetworkTestData(boolean sqlHandler) {
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
    accountCreated = new AccountCreated(new Id("0"), userId, 18);
    friendAdded = new FriendAdded(new Id("1"), friendId);
    messageSent = new MessageSent(new Id("3"), new Message(new Id("2"), userId, friendId, "hello"));
    // Command handlers
    handler = (sqlHandler) ? getSqlHandler() : getStatefulHandler();
  }

  private StatefulCommandHandler<User, UserCommand> getStatefulHandler() {
    return new StatefulCommandHandler<>(visitor, new UserEvolver(), new UserDecider(eventsIdGen));
  }

  private SqlCommandHandler<User, UserCommand> getSqlHandler() {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:~/test");
    ds.setUser("sa");
    ds.setPassword("sa");
    try {
      return new SqlCommandHandler<>(ds.getPooledConnection(),
                                     new SocialNetworkTransformer(),
                                     visitor,
                                     new UserEvolver(),
                                     new UserDecider(eventsIdGen),
                                     Schedulers.boundedElastic());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
