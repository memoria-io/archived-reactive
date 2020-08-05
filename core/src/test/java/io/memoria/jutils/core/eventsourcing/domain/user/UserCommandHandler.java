package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.AddFriend;
import io.memoria.jutils.core.eventsourcing.domain.user.UserCommand.SendMessage;
import io.memoria.jutils.core.generator.IdGenerator;
import reactor.core.publisher.Flux;

public record UserCommandHandler(IdGenerator idGenerator) implements CommandHandler<User, UserCommand, UserEvent> {
  @Override
  public Flux<UserEvent> apply(User user, UserCommand userCommand) {
    if (user instanceof OnlineUser onlineUser) {
      if (userCommand instanceof AddFriend addFriend)
        return addFriend.apply(onlineUser);
      if (userCommand instanceof SendMessage sendMessage)
        return sendMessage.apply(onlineUser, idGenerator.get());
    }
    return Flux.error(new Exception("Unknown event"));
  }
}
