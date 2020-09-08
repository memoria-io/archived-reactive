package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.tests;

import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Account;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserDecider;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.generator.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;

public class UserDeciderTest {
  // CommandHandler
  private static final AtomicInteger atomicInteger = new AtomicInteger();
  private static final IdGenerator idGen = () -> "0";
  private static final UserDecider decide = new UserDecider(idGen);
  // Data
  private static final String ALEX_Id = "alex";
  private static final String BOB_Id = "bob";
  private static final int ALEX_AGE = 19;
  private static final Account ALEX = new Account(ALEX_Id, ALEX_AGE);
  // Commands
  private static final AddFriend ADD_FRIEND = new AddFriend(ALEX_Id, BOB_Id);
  private static final SendMessage SEND_MESSAGE = new SendMessage(ALEX_Id, BOB_Id, "hello");
  private static final Message MESSAGE = new Message("0", ALEX_Id, BOB_Id, "hello");
  private static final MessageSent MESSAGE_SENT = new MessageSent("0", MESSAGE);

  @Test
  public void sendMessage() {
    // Given
    var alexWithFriend = ALEX.withNewFriend(BOB_Id);
    // When
    var events = decide.apply(alexWithFriend, SEND_MESSAGE).get();
    // Then
    Assertions.assertThat(events.head()).isEqualTo(MESSAGE_SENT);
  }

  @Test
  public void shouldAddFriend() {
    // When
    var events = decide.apply(ALEX, ADD_FRIEND).get();
    var user = new UserEvolver().apply(ALEX, events);
    // Then
    Assertions.assertThat(user).isEqualTo(ALEX.withNewFriend(BOB_Id));
  }

  @Test
  public void shouldNotAddFriend() {
    var events = decide.apply(ALEX.withNewFriend(BOB_Id), ADD_FRIEND);
    Assertions.assertThat(events.getCause()).isEqualTo(ALREADY_EXISTS);
  }
}
