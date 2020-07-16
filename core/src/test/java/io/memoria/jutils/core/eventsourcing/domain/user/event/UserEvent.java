package io.memoria.jutils.core.eventsourcing.domain.user.event;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record FriendAdded(String userId, String friendId) implements UserEvent {}

  record MessageCreated(String messageId, String from, String to, String body) implements UserEvent {}

  record MessageSeen(String conversationId, String messageId) implements UserEvent {}
}
