package io.memoria.jutils.r2es.socialnetwork.transformer;

import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.value.Id;
import io.memoria.jutils.r2es.socialnetwork.domain.Message;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.FriendAdded;
import io.memoria.jutils.r2es.socialnetwork.domain.UserEvent.MessageSent;
import io.vavr.control.Try;

public class SocialNetworkTransformer implements StringTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    var st = str.split(":");
    var type = st[0];
    return switch (type) {
      case "AccountCreated" -> Try.success((T) new AccountCreated(Id.of(st[1]), Id.of(st[2]), Integer.parseInt(st[3])));
      case "FriendAdded" -> Try.success((T) new FriendAdded(Id.of(st[1]), Id.of(st[2]), Id.of(st[3])));
      case "MessageSent" -> Try.success((T) new MessageSent(Id.of(st[1]),
                                                            Id.of(st[2]),
                                                            new Message(Id.of(st[3]),
                                                                        Id.of(st[4]),
                                                                        Id.of(st[5]),
                                                                        st[5])));
      default -> Try.failure(new Exception("Unknown type to be deserialized"));
    };
  }

  @Override
  public <T> Try<String> serialize(T t) {
    String type = t.getClass().getSimpleName();
    if (t instanceof AccountCreated e) {
      return Try.success(type + ":" + e.eventId().value() + ":" + e.aggId().value() + ":" + e.age());
    }
    if (t instanceof FriendAdded e) {
      return Try.success(type + ":" + e.eventId().value() + ":" + e.aggId().value() + ":" + e.friendId().value());
    }
    if (t instanceof MessageSent e) {
      return Try.success(
              type + ":" + e.eventId().value() + ":" + e.aggId().value() + ":" + e.message().id().value() + ":" +
              e.message().from().value() + ":" + e.message().to().value() + ":" + e.message().body());
    }
    return Try.failure(new Exception("Unknown type to be serialized"));
  }
}
