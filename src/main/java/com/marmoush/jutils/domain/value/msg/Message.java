package com.marmoush.jutils.domain.value.msg;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
  public final String key;
  public final String value;
  public final LocalDateTime creationDate;

  public Message(String key, String value) {
    this.key = key;
    this.value = value;
    this.creationDate = LocalDateTime.now();
  }

  public Message(String key, String value, LocalDateTime creationDate) {
    this.key = key;
    this.value = value;
    this.creationDate = creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Message message = (Message) o;
    return key.equals(message.key) && value.equals(message.value) && creationDate.equals(message.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, creationDate);
  }
}
