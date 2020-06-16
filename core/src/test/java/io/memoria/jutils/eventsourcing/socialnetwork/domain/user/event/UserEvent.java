package io.memoria.jutils.eventsourcing.socialnetwork.domain.user.event;

import io.memoria.jutils.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record MessageSeen(String conversationId, String messageId) implements UserEvent {}

  record MessageCreated(String messageId, String from, String to, String body) implements UserEvent {}

  record FriendAdded(String userId, String friendId) implements UserEvent {}
}
