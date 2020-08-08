package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEventHandler;
import io.memoria.jutils.core.generator.IdGenerator;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  // CommandHandler
  private static final AtomicInteger atomicInteger = new AtomicInteger();
  private static final IdGenerator idGen = () -> "0";
  private static final UserCommandHandler handler = new UserCommandHandler(idGen);
  // Data
  private static final String ALEX_NAME = "alex";
  private static final String BOB_NAME = "bob";
  private static final int ALEX_AGE = 19;
  private static final User ALEX = new User(ALEX_NAME, ALEX_AGE);
  // Commands
  private static final AddFriend ADD_FRIEND = new AddFriend(BOB_NAME);
  private static final SendMessage SEND_MESSAGE = new SendMessage(BOB_NAME, "hello");
  private static final Message MESSAGE = new Message("0", ALEX_NAME, BOB_NAME, "hello");
  private static final MessageSent MESSAGE_SENT = new MessageSent("0", MESSAGE);

  @Test
  public void shouldAddFriend() {
    // When
    var events = handler.apply(ALEX, ADD_FRIEND);
    var userMono = new UserEventHandler().apply(ALEX, events);
    // Then
    StepVerifier.create(userMono).expectNext(ALEX.withNewFriend(BOB_NAME)).expectComplete().verify();
  }

  @Test
  public void shouldNotAddFriend() {
    var events = handler.apply(ALEX.withNewFriend(BOB_NAME), ADD_FRIEND);
    var userMono = new UserEventHandler().apply(ALEX, events);
    StepVerifier.create(userMono).expectError(ALREADY_EXISTS.getClass()).verify();
  }

  @Test
  public void sendMessage() {
    // Given
    var alexWithFriend = ALEX.withNewFriend(BOB_NAME);
    // When
    var events = handler.apply(alexWithFriend, SEND_MESSAGE);
    // Then
    StepVerifier.create(events).expectNext(MESSAGE_SENT).expectComplete().verify();
  }
}
