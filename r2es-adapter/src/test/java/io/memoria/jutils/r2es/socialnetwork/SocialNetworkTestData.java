package io.memoria.jutils.r2es.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.r2es.R2CommandHandler;
import io.memoria.jutils.r2es.R2Utils;
import io.memoria.jutils.r2es.SqlCommandHandler;
import io.memoria.jutils.r2es.socialnetwork.domain.Message;
import io.memoria.jutils.r2es.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.r2es.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.r2es.socialnetwork.domain.UserDecider;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.eventsourcing.stateful.StatefulCommandHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.r2es.socialnetwork.transformer.SocialNetworkTransformer;
import io.r2dbc.spi.ConnectionFactories;
import org.h2.jdbcx.JdbcDataSource;
import reactor.core.scheduler.Schedulers;

import java.sql.SQLException;
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

  public SocialNetworkTestData(boolean isR2) throws SQLException {
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
    var transformer = new SocialNetworkTransformer();
    if (isR2) {
      var connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///testdb");
      handler = new R2CommandHandler<>(connectionFactory,
                                       transformer,
                                       new Visitor(),
                                       new UserEvolver(),
                                       new UserDecider(idGenerator));
    } else {
      JdbcDataSource ds = new JdbcDataSource();
      ds.setURL("jdbc:h2:~/test");
      ds.setUser("sa");
      ds.setPassword("sa");
      var pc = ds.getPooledConnection();
      handler = new SqlCommandHandler<>(pc,
                                        transformer,
                                        new Visitor(),
                                        new UserEvolver(),
                                        new UserDecider(idGenerator),
                                        Schedulers.boundedElastic());
    }

  }
}
