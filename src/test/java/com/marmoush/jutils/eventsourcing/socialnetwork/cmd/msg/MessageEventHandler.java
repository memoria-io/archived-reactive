package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.msg;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.EventHandler;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.InboxMessage;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.UserInbox;
import io.vavr.collection.List;

public class MessageEventHandler implements EventHandler<UserInbox, MessageEvent> {

  @Override
  public UserInbox apply(UserInbox userInbox, MessageEvent messageEvent) {
    return null;
  }
}
