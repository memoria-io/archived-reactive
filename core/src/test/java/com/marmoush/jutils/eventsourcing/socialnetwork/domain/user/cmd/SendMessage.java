package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.cmd;

import java.util.Objects;

public final class SendMessage implements UserCommand {
  public final String fromUserId;
  public final String toUserId;
  public final String message;

  public SendMessage(String fromUserId, String toUserId, String message) {
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;
    this.message = message;
  }

  //    @Override
  //    public Try<List<Event<User>>> apply(User user) {
  //      var validate = (user.friends.contains(this.toUserId)) ? Try.success(null) : Try.failure(NOT_FOUND);
  //      return validate.map(v -> {
  //        var created = new MessageCreated(fromUserId, toUserId, message);
  //        return List.of(created);
  //      });
  //    }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    SendMessage that = (SendMessage) o;
    return fromUserId.equals(that.fromUserId) && toUserId.equals(that.toUserId) && message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fromUserId, toUserId, message);
  }
}