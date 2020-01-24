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
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo.MessageCmdRepo;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo.UserCmdRepo;
import com.marmoush.jutils.general.domain.port.IdGenerator;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import static com.marmoush.jutils.utils.functional.VavrUtils.tryMap;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserCommandService implements CommandService {
  private final UserCmdRepo ucr;
  private final MessageCmdRepo mcr;
  private final IdGenerator idGen;

  public UserCommandService(UserCmdRepo ucr, MessageCmdRepo mcr, IdGenerator idGenerator) {
    this.ucr = ucr;
    this.mcr = mcr;
    this.idGen = idGenerator;
  }

  @Override
  public Mono<Try<List<Event>>> handle(Command cmdReq) {
    Match(cmdReq).of(Case($(instanceOf(CreateUser.class)), e -> createUserAction(e)),
                     Case($(instanceOf(SendMessage.class)), e -> sendMessageAction(e)));
    return null;
  }

  @Override
  public Try<Void> evolve(Event event) {
    return null;
  }

  private Mono<Try<List<Event>>> createUserAction(CreateUser s) {
    return ucr.exists(s.userName)
              .map(t -> t.map(v -> new UserCreated(s.eventId, s.userName, s.userName, s.age)))
              .map(tryMap(List::of));

  }

  private Mono<Try<List<Event>>> sendMessageAction(SendMessage m) {
    return ucr.exists(m.fromUserId)
              .zipWith(ucr.exists(m.toUserId))
              .map(t -> t.getT1().flatMap(t1 -> t.getT2()))
              .map(b -> Try.success(List.of(toMessageCreated(m), toMessageSent(m), toMessageReceived(m))));
  }

  private static MessageCreated toMessageCreated(SendMessage me) {
    return new MessageCreated(me.eventId, me.fromUserId, me.toUserId, me.message);
  }

  private static MessageSent toMessageSent(SendMessage me) {
    return new MessageSent(me.eventId, me.fromUserId);
  }

  private static MessageReceived toMessageReceived(SendMessage me) {
    return new MessageReceived(me.eventId, me.toUserId);
  }
}
