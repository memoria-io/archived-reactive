package com.marmoush.jutils.adapter.eventsourcing;

import com.marmoush.jutils.domain.port.eventsourcing.cmd.WriteRequest;

public class Commands {
  public static class CreateUser implements WriteRequest {
    public final String userName;

    public CreateUser(String userName) {
      this.userName = userName;
    }
  }

  public static class AddFriend implements WriteRequest {
    public final String userId;
    public final String friendId;

    public AddFriend(String userId, String friendId) {
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static class SendMessage implements WriteRequest {
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
