package io.memoria.jutils.jackson.transformer.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.memoria.jutils.core.value.Version;

import java.io.IOException;

public final class VersionTransformer {

  public static class VersionDeserializer extends JsonDeserializer<Version> {
    @Override
    public Version deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Version.from(p.readValueAs(String.class)).getOrElseThrow(t -> new IOException(t.getMessage()));
    }
  }

  public static class VersionSerializer extends JsonSerializer<Version> {
    @Override
    public void serialize(Version value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(value.major() + "." + value.minor() + "." + value.patch());
    }
  }

  private VersionTransformer() {}
}
