package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {

  record AccountCreated(String eventId, String aggId, int age) implements UserEvent {}

  record FriendAdded(String eventId, String aggId, String friendId) implements UserEvent {}

  record MessageSent(String eventId, String aggId, Message message) implements UserEvent {}
}
