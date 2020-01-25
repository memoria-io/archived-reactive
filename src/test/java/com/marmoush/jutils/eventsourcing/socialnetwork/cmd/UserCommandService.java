package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.CommandHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Commands.UserCommand;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.Events.UserEvent;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.adapter.UserCommandHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value.User;
import com.marmoush.jutils.general.domain.port.IdGenerator;

public class UserCommandService {
  private final IdGenerator idGen;
  private final CommandHandler<User, UserCommand, UserEvent> userHandler;

  public UserCommandService(IdGenerator idGenerator) {
    this.idGen = idGenerator;
    this.userHandler = new UserCommandHandler(idGenerator);
  }
}
