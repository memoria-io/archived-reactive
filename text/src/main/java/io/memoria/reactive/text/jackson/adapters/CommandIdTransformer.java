package io.memoria.reactive.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.reactive.core.eventsourcing.CommandId;

import java.io.IOException;

public final class CommandIdTransformer {

  private CommandIdTransformer() {}

  public static class CommandIdDeserializer extends JsonDeserializer<CommandId> {
    @Override
    public CommandId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return CommandId.of(p.readValueAs(String.class));
    }
  }

  public static class CommandIdSerializer extends JsonSerializer<CommandId> {
    @Override
    public void serialize(CommandId id, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(id.value());
    }
  }
}
