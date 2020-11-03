package io.memoria.jutils.jackson.transformer.id;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.memoria.jutils.core.value.Id;

import java.io.IOException;

public class IdDeserializer extends JsonDeserializer<Id> {
  @Override
  public Id deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return new Id(p.readValueAs(String.class));
  }
}
