package io.memoria.jutils.adapter.transformer.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

  @Override
  public Duration read(JsonReader in) throws IOException {
    return Duration.parse(in.nextString());
  }

  @Override
  public void write(JsonWriter out, Duration duration) throws IOException {
    out.value(duration.toString());
  }
}
