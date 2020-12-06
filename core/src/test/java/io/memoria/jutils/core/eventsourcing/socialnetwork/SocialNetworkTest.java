package io.memoria.jutils.core.eventsourcing.socialnetwork;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

class SocialNetworkTest {
  private static final JdbcDataSource ds;

  static {
    ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:~/test");
    ds.setUser("sa");
    ds.setPassword("sa");
  }

  @Test
  void sqlHandlerFailurePath() throws SQLException {
    SocialNetworkSuite.failurePath(new SocialNetworkTestData(some(ds.getPooledConnection())));
  }

  @Test
  void sqlHandlerHappyPath() throws SQLException {
    SocialNetworkSuite.happyPath(new SocialNetworkTestData(some(ds.getPooledConnection())));
  }

  @Test
  void sqlHandlerManyCommands() throws SQLException {
    SocialNetworkSuite.manyCommands(new SocialNetworkTestData(some(ds.getPooledConnection())));
  }

  @Test
  void sqlHandlerOneCommand() throws SQLException {
    SocialNetworkSuite.oneCommand(new SocialNetworkTestData(some(ds.getPooledConnection())));
  }

  @Test
  void statefulHandlerHappyPath() throws SQLException {
    SocialNetworkSuite.happyPath(new SocialNetworkTestData(none()));
    SocialNetworkSuite.oneCommand(new SocialNetworkTestData(none()));
    SocialNetworkSuite.failurePath(new SocialNetworkTestData(none()));
    SocialNetworkSuite.manyCommands(new SocialNetworkTestData(none()));
  }
}
