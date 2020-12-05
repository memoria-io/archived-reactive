package io.memoria.jutils.core.eventsourcing.socialnetwork;

import org.junit.jupiter.api.Test;

class SocialNetworkTest {
  @Test
  void sqlHandler() {
    SocialNetworkSuite.applyAllCommands(new SocialNetworkTestData(true));
    SocialNetworkSuite.applyOneCommand(new SocialNetworkTestData(true));
    SocialNetworkSuite.applyTwice(new SocialNetworkTestData(true));
  }

  @Test
  void statefulHandler() {
    SocialNetworkSuite.applyAllCommands(new SocialNetworkTestData(false));
    SocialNetworkSuite.applyOneCommand(new SocialNetworkTestData(false));
    SocialNetworkSuite.applyTwice(new SocialNetworkTestData(false));
  }
}
