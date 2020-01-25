package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.Command;

public class Commands {
  private Commands() {}

  public static class UserCommand extends Command {
    public UserCommand(String id) {
      super(id);
    }
  }

  public static class CreateUser extends UserCommand {
    public final String userName;
    public final int age;

    public CreateUser(String eventId, String userName, int age) {
      super(eventId);
      this.userName = userName;
      this.age = age;
    }
  }

  public static class AddFriend extends UserCommand {
    public final String userId;
    public final String friendId;

    public AddFriend(String eventId, String userId, String friendId) {
      super(eventId);
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static class SendMessage extends UserCommand {
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
