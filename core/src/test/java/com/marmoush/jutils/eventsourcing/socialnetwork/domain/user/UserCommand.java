package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user;

import com.marmoush.jutils.eventsourcing.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.*;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.Objects;

import static com.marmoush.jutils.core.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.core.domain.error.NotFound.NOT_FOUND;

public abstract class UserCommand implements Command<User> {
  private UserCommand() { }

  public static final class CreateUser extends UserCommand {
    public final String userName;
    public final int age;

    public CreateUser(String userName, int age) {
      this.userName = userName;
      this.age = age;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      if (!super.equals(o))
        return false;
      CreateUser that = (CreateUser) o;
      return age == that.age && userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), userName, age);
    }

    @Override
    public Try<List<Event<User>>> apply(User user) {
      return null;
    }
  }

  public static final class AddFriend extends UserCommand {
    public final String userId;
    public final String friendId;

    public AddFriend(String userId, String friendId) {

      this.userId = userId;
      this.friendId = friendId;
    }

    @Override
    public Try<List<Event<User>>> apply(User user) {
      var validate = (user.friends.contains(friendId)) ? Try.failure(ALREADY_EXISTS) : Try.success(null);
      return validate.map(v -> List.of(new FriendAdded(userId, friendId)));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      if (!super.equals(o))
        return false;
      AddFriend addFriend = (AddFriend) o;
      return userId.equals(addFriend.userId) && friendId.equals(addFriend.friendId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), userId, friendId);
    }
  }

  public static final class SendMessage extends UserCommand {
    public final String fromUserId;
    public final String toUserId;
    public final String message;

    public SendMessage(String fromUserId, String toUserId, String message) {

      this.fromUserId = fromUserId;
      this.toUserId = toUserId;
      this.message = message;
    }

    @Override
    public Try<List<Event<User>>> apply(User user) {
      var validate = (user.friends.contains(this.toUserId)) ? Try.success(null) : Try.failure(NOT_FOUND);
      return validate.map(v -> {
        var created = new MessageCreated(fromUserId, toUserId, message);
        return List.of(created);
      });
    }

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
}
