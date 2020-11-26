package io.memoria.jutils.core.eventsourcing.socialnetwork.transformer;

import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.Message;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.socialnetwork.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.value.Id;
import io.vavr.control.Try;

public class SocialNetworkTransformer implements StringTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    var st = str.split(":");
    var type = st[0];
    return switch (type) {
      case "AccountCreated" -> Try.success((T) new AccountCreated(new Id(st[1]),
                                                                  new Id(st[2]),
                                                                  Integer.parseInt(st[3])));
      case "FriendAdded" -> Try.success((T) new FriendAdded(new Id(st[1]), new Id(st[2])));
      case "MessageSent" -> Try.success((T) new MessageSent(new Id(st[1]),
                                                            new Message(new Id(st[2]),
                                                                        new Id(st[3]),
                                                                        new Id(st[4]),
                                                                        st[5])));
      default -> Try.failure(new Exception("Unknown type to be deserialized"));
    };
  }

  @Override
  public <T> Try<String> serialize(T t) {
    String result = t.getClass().getSimpleName();
    if (t instanceof AccountCreated e) {
      return Try.success(result + ":" + e.id().value() + ":" + e.age());
    }
    if (t instanceof FriendAdded e) {
      return Try.success(result + ":" + e.id().value() + ":" + e.friendId().value());
    }
    if (t instanceof MessageSent e) {
      return Try.success(
              result + ":" + e.id().value() + ":" + e.message().id().value() + ":" + e.message().from().value() + ":" +
              e.message().to().value() + ":" + e.message().body());
    }
    return Try.failure(new Exception("Unknown type to be serialized"));
  }
}
