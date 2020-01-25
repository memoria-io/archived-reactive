package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.Command;

public abstract class UserCommand extends Command {

  private UserCommand(String id) {
    super(id);
  }

  public static final class CreateUser extends UserCommand {
    public final String userName;
    public final int age;

    public CreateUser(String eventId, String userName, int age) {
      super(eventId);
      this.userName = userName;
      this.age = age;
    }
  }

  public static final class AddFriend extends UserCommand {
    public final String userId;
    public final String friendId;

    public AddFriend(String eventId, String userId, String friendId) {
      super(eventId);
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static final class SendMessage extends UserCommand {
    public final String fromUserId;
    public final String toUserId;
    public final String message;

    public SendMessage(String eventId, String fromUserId, String toUserId, String message) {
      super(eventId);
      this.fromUserId = fromUserId;
      this.toUserId = toUserId;
      this.message = message;
    }
  }
}
