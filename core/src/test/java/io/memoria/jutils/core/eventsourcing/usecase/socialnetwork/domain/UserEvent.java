package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.value.Id;

public interface UserEvent extends Event {

  record AccountCreated(Id id, Id accountId, int age) implements UserEvent {}

  record FriendAdded(Id id, Id friendId) implements UserEvent {}

  record MessageSent(Id id, Message message) implements UserEvent {}
}
