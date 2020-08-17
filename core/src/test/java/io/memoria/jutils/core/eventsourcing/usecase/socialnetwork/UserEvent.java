package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record FriendAdded(String eventId, String aggId, String friendId) implements UserEvent {}

  record MessageSent(String eventId, String aggId, Message message) implements UserEvent {}
}
