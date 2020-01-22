package com.marmoush.jutils.adapter.eventsourcing.cmd;

import com.marmoush.jutils.adapter.eventsourcing.cmd.Commands.CreateUser;
import com.marmoush.jutils.adapter.eventsourcing.cmd.Commands.SendMessage;
import com.marmoush.jutils.adapter.eventsourcing.cmd.Events.MessageCreated;
import com.marmoush.jutils.adapter.eventsourcing.cmd.Events.MessageReceived;
import com.marmoush.jutils.adapter.eventsourcing.cmd.Events.MessageSent;
import com.marmoush.jutils.adapter.eventsourcing.cmd.Events.UserCreated;
import com.marmoush.jutils.adapter.eventsourcing.cmd.entity.MessageEntity;
import com.marmoush.jutils.adapter.eventsourcing.cmd.entity.UserEntity;
import com.marmoush.jutils.adapter.eventsourcing.cmd.repo.MessageCmdRepo;
import com.marmoush.jutils.adapter.eventsourcing.cmd.repo.UserCmdRepo;
import com.marmoush.jutils.adapter.eventsourcing.cmd.value.Message;
import com.marmoush.jutils.adapter.eventsourcing.cmd.value.User;
import com.marmoush.jutils.domain.port.IdGenerator;
import com.marmoush.jutils.domain.port.eventsourcing.Event;
import com.marmoush.jutils.domain.port.eventsourcing.cmd.Command;
import com.marmoush.jutils.domain.port.eventsourcing.cmd.CommandService;
import com.marmoush.jutils.utils.functional.ReactorVavrUtils.MFn1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import static com.marmoush.jutils.utils.functional.VavrUtils.tryMap;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserCmdService implements CommandService {
  private final UserCmdRepo ucr;
  private final MessageCmdRepo mcr;
  private final IdGenerator idGen;

  public UserCmdService(UserCmdRepo ucr, MessageCmdRepo mcr, IdGenerator idGenerator) {
    this.ucr = ucr;
    this.mcr = mcr;
    this.idGen = idGenerator;
  }

  @Override
  public Mono<Try<List<Event>>> write(Command cmdReq) {
    return Match(cmdReq).of(Case($(instanceOf(CreateUser.class)), createUserAction()),
                            Case($(instanceOf(SendMessage.class)), sendMessageAction()));
  }

  private MFn1<CreateUser, List<Event>> createUserAction() {
    return s -> ucr.create(new UserEntity(s.userName, new User(s.userName, s.age)))
                   .map(tryMap(UserCmdService::toUserCreated))
                   .map(tryMap(List::of));
  }

  private MFn1<SendMessage, List<Event>> sendMessageAction() {
    return s -> mcr.create(new MessageEntity(idGen.generate(), new Message(s.fromUserId, s.toUserId, s.message)))
                   .map(a -> a.map(m -> List.of(toMessageCreated(m), toMessageSent(m), toMessageReceived(m))));
  }

  private static UserCreated toUserCreated(UserEntity ue) {
    return new UserCreated(ue.id, ue.value.name, ue.value.age);
  }

  private static MessageCreated toMessageCreated(MessageEntity me) {
    return new MessageCreated(me.id, me.value.from, me.value.to, me.value.body);
  }

  private static MessageSent toMessageSent(MessageEntity me) {
    return new MessageSent(me.id, me.value.from);
  }

  private static MessageReceived toMessageReceived(MessageEntity me) {
    return new MessageReceived(me.id, me.value.to);
  }
}
