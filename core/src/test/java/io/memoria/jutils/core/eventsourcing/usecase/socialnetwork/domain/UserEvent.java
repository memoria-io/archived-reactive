package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {

  record AccountCreated(String id, String aggId, int age) implements UserEvent {}

  record FriendAdded(String id, String aggId, String friendId) implements UserEvent {}

  record MessageSent(String id, String aggId, Message message) implements UserEvent {}
}
