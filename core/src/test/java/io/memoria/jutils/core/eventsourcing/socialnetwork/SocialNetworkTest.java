package io.memoria.jutils.core.eventsourcing.socialnetwork;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
  void sqlHandlerFailurePath() {
    Assertions.assertAll(() -> SocialNetworkSuite.failurePath(new SocialNetworkTestData(some(ds.getPooledConnection()))));
  }

  @Test
  void sqlHandlerHappyPath() {
    Assertions.assertAll(() -> SocialNetworkSuite.happyPath(new SocialNetworkTestData(some(ds.getPooledConnection()))));
  }

  @Test
  void sqlHandlerManyCommands() {
    Assertions.assertAll(() -> SocialNetworkSuite.manyCommands(new SocialNetworkTestData(some(ds.getPooledConnection()))));
  }

  @Test
  void sqlHandlerOneCommand() {
    Assertions.assertAll(() -> SocialNetworkSuite.oneCommand(new SocialNetworkTestData(some(ds.getPooledConnection()))));
  }

  @Test
  void statefulHandlerHappyPath() {
    Assertions.assertAll(() -> {
      SocialNetworkSuite.happyPath(new SocialNetworkTestData(none()));
      SocialNetworkSuite.oneCommand(new SocialNetworkTestData(none()));
      SocialNetworkSuite.failurePath(new SocialNetworkTestData(none()));
      SocialNetworkSuite.manyCommands(new SocialNetworkTestData(none()));
    });
  }
}
