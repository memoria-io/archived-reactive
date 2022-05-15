package io.memoria.reactive.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.reactive.core.eventsourcing.StateId;

import java.io.IOException;

public final class StateIdTransformer {

  private StateIdTransformer() {}

  public static class StateIdDeserializer extends JsonDeserializer<StateId> {
    @Override
    public StateId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return StateId.of(p.readValueAs(String.class));
    }
  }

  public static class StateIdSerializer extends JsonSerializer<StateId> {
    @Override
    public void serialize(StateId id, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(id.value());
    }
  }
}
