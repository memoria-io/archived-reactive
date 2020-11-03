package io.memoria.jutils.jackson.transformer.id;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.jutils.core.value.Id;

import java.io.IOException;

public class IdSerializer extends JsonSerializer<Id> {
  @Override
  public void serialize(Id value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeString(value.id());
  }
}
