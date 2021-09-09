package io.memoria.reactive.core.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializableTransformerTest {
  @Test
  void serializableTest() {
    var serTrans = new SerializableTransformer();
    var personObj = new Person("bob", 19, new Location(10, 20));
    var str = serTrans.serialize(personObj).block();
    var obj = serTrans.deserialize(str, Person.class).block();
    assertEquals(personObj, obj);
  }
}
