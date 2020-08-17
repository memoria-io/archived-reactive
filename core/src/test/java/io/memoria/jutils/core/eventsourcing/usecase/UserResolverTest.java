package io.memoria.jutils.core.eventsourcing.usecase;

import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.Message;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.User;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.UserEvolver;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.UserResolver;
import io.memoria.jutils.core.generator.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;

public class UserResolverTest {
  // CommandHandler
  private static final AtomicInteger atomicInteger = new AtomicInteger();
  private static final IdGenerator idGen = () -> "0";
  private static final UserResolver decide = new UserResolver(idGen);
  // Data
  private static final String ALEX_NAME = "alex";
  private static final String BOB_NAME = "bob";
  private static final int ALEX_AGE = 19;
  private static final User ALEX = new User(ALEX_NAME, ALEX_AGE);
  // Commands
  private static final AddFriend ADD_FRIEND = new AddFriend(BOB_NAME);
  private static final SendMessage SEND_MESSAGE = new SendMessage(BOB_NAME, "hello");
  private static final Message MESSAGE = new Message("0", ALEX_NAME, BOB_NAME, "hello");
  private static final MessageSent MESSAGE_SENT = new MessageSent("0", ALEX.id(), MESSAGE);

  @Test
  public void sendMessage() {
    // Given
    var alexWithFriend = ALEX.withNewFriend(BOB_NAME);
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
    Assertions.assertThat(user).isEqualTo(ALEX.withNewFriend(BOB_NAME));
  }

  @Test
  public void shouldNotAddFriend() {
    var events = decide.apply(ALEX.withNewFriend(BOB_NAME), ADD_FRIEND);
    Assertions.assertThat(events.getCause()).isEqualTo(ALREADY_EXISTS);
  }
}
