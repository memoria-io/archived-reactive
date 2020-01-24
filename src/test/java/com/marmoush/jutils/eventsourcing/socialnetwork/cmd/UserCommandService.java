package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;
import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.Command;
import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.CommandService;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Commands.CreateUser;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Commands.SendMessage;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Events.MessageCreated;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Events.MessageReceived;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Events.MessageSent;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Events.UserCreated;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.entity.MessageEntity;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.entity.NotificationEntity;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.entity.UserEntity;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo.MessageCmdRepo;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo.UserCmdRepo;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo.UserNotificationsRepo;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value.Message;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value.Notification;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value.User;
import com.marmoush.jutils.general.domain.port.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import static com.marmoush.jutils.utils.functional.VavrUtils.tryMap;
import static com.marmoush.jutils.utils.functional.VavrUtils.tryVoid;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserCommandService implements CommandService {
  private final UserCmdRepo ucr;
  private final UserNotificationsRepo unr;
  private final MessageCmdRepo mcr;
  private final IdGenerator idGen;

  public UserCommandService(UserCmdRepo ucr, UserNotificationsRepo unr, MessageCmdRepo mcr, IdGenerator idGenerator) {
    this.ucr = ucr;
    this.unr = unr;
    this.mcr = mcr;
    this.idGen = idGenerator;
  }

  @Override
  public Mono<Try<List<Event>>> handle(Command cmdReq) {
    return Match(cmdReq).of(Case($(instanceOf(CreateUser.class)), e -> createUserAction(e)),
                            Case($(instanceOf(SendMessage.class)), e -> sendMessageAction(e)));
  }

  @Override
  public Mono<Try<Void>> evolve(Event event) {
    return Match(event).of(Case($(instanceOf(UserCreated.class)), e -> userCreatedEffect(e)),
                           Case($(instanceOf(MessageCreated.class)), e -> messageCreatedEffect(e)),
                           Case($(instanceOf(MessageSent.class)), e -> messageSentEffect(e)),
                           Case($(instanceOf(MessageReceived.class)), e -> messageReceivedEffect(e)));
  }

  private Mono<Try<Void>> userCreatedEffect(UserCreated u) {
    return ucr.create(new UserEntity(u.userId, new User(u.name, u.age))).map(tryVoid());
  }

  private Mono<Try<Void>> messageCreatedEffect(MessageCreated m) {
    return mcr.create(new MessageEntity(m.msgId, new Message(m.from, m.to, m.body))).map(tryVoid());
  }

  private Mono<Try<Void>> messageSentEffect(MessageSent m) {
    return unr.create(new NotificationEntity(m.msgId, Notification.messageSentNote(m.msgId))).map(tryVoid());
  }

  private Mono<Try<Void>> messageReceivedEffect(MessageReceived m) {
    return unr.create(new NotificationEntity(m.msgId, Notification.messageReceivedNote(m.msgId))).map(tryVoid());
  }

  private Mono<Try<List<Event>>> createUserAction(CreateUser s) {
    return ucr.exists(s.userName)
              .map(t -> t.map(v -> new UserCreated(s.flowId, s.userName, s.userName, s.age)))
              .map(tryMap(List::of));

  }

  private Mono<Try<List<Event>>> sendMessageAction(SendMessage m) {
    return ucr.exists(m.fromUserId)
              .zipWith(ucr.exists(m.toUserId))
              .map(t -> t.getT1().flatMap(t1 -> t.getT2()))
              .map(b -> {
                String id = idGen.generate();
                return Try.success(List.of(toMessageCreated(m, id), toMessageSent(m, id), toMessageReceived(m, id)));
              });
  }

  private MessageCreated toMessageCreated(SendMessage me, String msgId) {
    return new MessageCreated(me.flowId, msgId, me.fromUserId, me.toUserId, me.message);
  }

  private MessageSent toMessageSent(SendMessage me, String msgId) {
    return new MessageSent(me.flowId, msgId, me.fromUserId);
  }

  private MessageReceived toMessageReceived(SendMessage me, String msgId) {
    return new MessageReceived(me.flowId, msgId, me.toUserId);
  }
}
