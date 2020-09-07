package io.memoria.jutils.adapter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationGsonAdapter extends TypeAdapter<Duration> {
  public static GsonBuilder register(GsonBuilder gsonBuilder) {
    return gsonBuilder.registerTypeAdapter(Duration.class, new DurationGsonAdapter());
  }

  @Override
  public Duration read(JsonReader in) throws IOException {
    return Duration.parse(in.nextString());
  }

  @Override
  public void write(JsonWriter out, Duration value) throws IOException {
    out.jsonValue(value.toString());
  }
}
