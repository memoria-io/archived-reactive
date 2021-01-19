package io.memoria.jutils.jackson.transformer.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.jutils.core.id.Id;

import java.io.IOException;

public final class IdTransformer {

  public static class IdDeserializer extends JsonDeserializer<Id> {
    @Override
    public Id deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Id.of(p.readValueAs(String.class));
    }
  }

  public static class IdSerializer extends JsonSerializer<Id> {
    @Override
    public void serialize(Id value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(value.value());
    }
  }

  private IdTransformer() {}
}
