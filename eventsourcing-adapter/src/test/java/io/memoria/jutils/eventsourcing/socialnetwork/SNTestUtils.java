package io.memoria.jutils.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.core.id.IdGenerator;
import io.memoria.jutils.eventsourcing.memory.InMemoryCommandHandler;
import io.memoria.jutils.eventsourcing.r2.R2CommandHandler;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.User;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserDecider;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.eventsourcing.socialnetwork.transformer.SNTransformer;
import io.memoria.jutils.eventsourcing.sql.SqlCommandHandler;
import io.r2dbc.spi.ConnectionFactories;
import org.h2.jdbcx.JdbcDataSource;
import reactor.core.scheduler.Schedulers;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class SNTestUtils {

  public static CommandHandler<UserCommand> memCH(IdGenerator idGenerator) {
    var db = new ConcurrentHashMap<Id, User>();
    return new InMemoryCommandHandler<>(db, new Visitor(), new UserEvolver(), new UserDecider(idGenerator));
  }

  public static CommandHandler<UserCommand> r2CH(IdGenerator idGenerator) {
    var connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///testR2");
    return new R2CommandHandler<>(connectionFactory,
                                  new SNTransformer(),
                                  new Visitor(),
                                  new UserEvolver(),
                                  new UserDecider(idGenerator));
  }

  public static CommandHandler<UserCommand> sqlCH(IdGenerator idGenerator) throws SQLException {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:///testJDBC");
    ds.setUser("sa");
    ds.setPassword("sa");
    var pc = ds.getPooledConnection();
    return new SqlCommandHandler<>(pc,
                                   new SNTransformer(),
                                   new Visitor(),
                                   new UserEvolver(),
                                   new UserDecider(idGenerator),
                                   Schedulers.boundedElastic());
  }

  private SNTestUtils() {}
}
