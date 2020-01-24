package com.marmoush.jutils.general.adapter.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.cmd.Command;

public class Commands {
  private Commands() {}

  public static class CreateUser implements Command {
    public final String userName;
    public final int age;

    public CreateUser(String userName, int age) {
      this.userName = userName;
      this.age = age;
    }
  }

  public static class AddFriend implements Command {
    public final String userId;
    public final String friendId;

    public AddFriend(String userId, String friendId) {
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static class SendMessage implements Command {
    public final String fromUserId;
    public final String toUserId;
    public final String message;

    public SendMessage(String fromUserId, String toUserId, String message) {
      this.fromUserId = fromUserId;
      this.toUserId = toUserId;
      this.message = message;
    }
  }
}
