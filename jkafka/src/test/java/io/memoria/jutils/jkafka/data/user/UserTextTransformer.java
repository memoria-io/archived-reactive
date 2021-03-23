package io.memoria.jutils.jkafka.data.user;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.control.Try;

/*
 * Dummy impl and not how textTransformer should be used
 */
public class UserTextTransformer implements TextTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    var user = str.split(":");
    return Try.success((T) new UserEvent.UserCreated(Id.of(user[0]), Id.of(user[1]), user[2]));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    var user = (UserEvent.UserCreated) t;
    return Try.success("%s:%s:%s".formatted(user.eventId().value(), user.userId(), user.name()));
  }
}