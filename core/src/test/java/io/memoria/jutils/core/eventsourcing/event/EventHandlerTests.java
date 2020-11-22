package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserDecider;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class EventHandlerTests {
  private static final AtomicInteger atomic = new AtomicInteger();
  private static final IdGenerator idGen = () -> new Id(atomic.getAndIncrement() + "");
  private static final IdGenerator eventsIdGen = () -> new Id("event_0");
  private final CommandHandler<User, UserCommand> handler;
  private final EventStore eventStore;

  public EventHandlerTests(EventStore eventStore) {
    this.eventStore = eventStore;
    handler = new CommandHandler<>(this.eventStore, new UserEvolver(), new UserDecider(eventsIdGen), new Visitor());
  }

  public void singleApply() {
    // Given
    var userId = new Id("user_" + new Random().nextInt(1000));
    var topic = userId.value();
    var friendId = new Id("1");
    var create = new CreateAccount(userId, 18);
    var add = new AddFriend(userId, friendId);
    var send = new SendMessage(userId, friendId, "hello");
    // When
    var single1 = handler.apply(topic, create);
    var single2 = handler.apply(topic, add);
    var single3 = handler.apply(topic, send);
    // Then
    var accountCreated = new AccountCreated(eventsIdGen.get(), userId, 18);
    var friendAdded = new FriendAdded(eventsIdGen.get(), friendId);
    var messageSent = new MessageSent(eventsIdGen.get(), new Message(eventsIdGen.get(), userId, friendId, "hello"));
    StepVerifier.create(single1.then(single2).then(single3)).expectComplete().verify();
    StepVerifier.create(eventStore.stream(topic))
                .expectNext(accountCreated, friendAdded, messageSent)
                .expectComplete()
                .verify();
  }

  public void fluxApply() {
    // Given
    var userId = new Id("user_" + new Random().nextInt(1000));
    var topic = userId.value();
    var friendId = new Id("1");
    var create = new CreateAccount(userId, 18);
    var add = new AddFriend(userId, friendId);
    var send = new SendMessage(userId, friendId, "hello");
    // When
    var all = handler.apply(topic, List.of(create, add, send));
    // Then
    var accountCreated = new AccountCreated(eventsIdGen.get(), userId, 18);
    var friendAdded = new FriendAdded(eventsIdGen.get(), friendId);
    var messageSent = new MessageSent(eventsIdGen.get(), new Message(eventsIdGen.get(), userId, friendId, "hello"));
    StepVerifier.create(all).expectComplete().verify();
    StepVerifier.create(eventStore.stream(topic))
                .expectNext(accountCreated, friendAdded, messageSent)
                .expectComplete()
                .verify();
  }

  public void shouldProduceInvalidOperation() {
    // Given
    var userId = new Id("user_" + new Random().nextInt(1000));
    var topic = userId.value();
    var cmd = new CreateAccount(userId, 18);
    // When
    var handleMono = handler.apply(topic, Flux.just(cmd, cmd));
    // Then
    StepVerifier.create(handleMono).expectError(ESInvalidOperation.class).verify();
  }

  public void runAll() {
//    singleApply();
    fluxApply();
//    shouldProduceInvalidOperation();
  }
}
