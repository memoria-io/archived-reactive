package io.memoria.jutils.adapter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Period;

public class PeriodGsonAdapter extends TypeAdapter<Period> {
  public static GsonBuilder register(GsonBuilder gsonBuilder) {
    return gsonBuilder.registerTypeAdapter(Period.class, new PeriodGsonAdapter());
  }

  @Override
  public Period read(JsonReader in) throws IOException {
    return Period.parse(in.nextString());
  }

  @Override
  public void write(JsonWriter out, Period value) throws IOException {
    out.jsonValue(value.toString());
  }
}
