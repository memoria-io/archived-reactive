package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandHandlerTest {

  @Test
  public void applyAllCommands() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var eventTry = testData.handler.apply(testData.topic, List.of(testData.create, testData.add, testData.send)).get();
    // Then
    Assertions.assertEquals(List.of(testData.accountCreated, testData.friendAdded, testData.messageSent), eventTry);
  }

  @Test
  public void applyOneCommand() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var events = testData.handler.apply(testData.topic, testData.create).get();
    // Then
    Assertions.assertEquals(List.of(testData.accountCreated), events);
  }

  @Test
  public void applyTwice() {
    // Given
    var testData = new SocialNetworkTestData();
    // When
    var events = testData.handler.apply(testData.topic, List.of(testData.create, testData.create));
    // Then
    Assertions.assertTrue(events.getCause() instanceof ESInvalidOperation);
  }
}
