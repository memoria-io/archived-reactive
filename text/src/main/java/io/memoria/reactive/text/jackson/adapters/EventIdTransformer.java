package io.memoria.reactive.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.reactive.core.eventsourcing.EventId;

import java.io.IOException;

public final class EventIdTransformer {

  private EventIdTransformer() {}

  public static class EventIdDeserializer extends JsonDeserializer<EventId> {
    @Override
    public EventId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return EventId.of(p.readValueAs(String.class));
    }
  }

  public static class EventIdSerializer extends JsonSerializer<EventId> {
    @Override
    public void serialize(EventId id, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(id.value());
    }
  }
}
