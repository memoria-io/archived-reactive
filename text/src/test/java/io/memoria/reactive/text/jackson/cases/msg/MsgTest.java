package io.memoria.reactive.text.jackson.cases.msg;

import io.memoria.reactive.core.stream.Msg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.memoria.reactive.text.jackson.TestDeps.fileUtils;
import static io.memoria.reactive.text.jackson.TestDeps.json;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MsgTest {
  private static final String MSG_JSON = fileUtils.read("cases/msg/json/Msg.json").block();

  @Test
  @DisplayName("My message nested values should be deserialized correctly")
  void deserialize() {
    // When
    var msgObj = json.deserialize(MSG_JSON, Msg.class).get();
    // Then
    assertEquals("msg_id", msgObj.id().value());
    assertEquals("hello world", msgObj.body());
  }

  @Test
  @DisplayName("My message should be serialized to correct values")
  void serialize() {
    // When
    var myMessageString = json.serialize(Msg.of("msg_id", "hello world")).get();
    // Then
    assertEquals(MSG_JSON, myMessageString);
  }
}
