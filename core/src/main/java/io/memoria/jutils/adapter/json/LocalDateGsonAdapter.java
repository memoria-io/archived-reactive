package io.memoria.jutils.adapter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateGsonAdapter extends TypeAdapter<LocalDate> {
  private final DateTimeFormatter dateFormat;

  public static GsonBuilder register(GsonBuilder gsonBuilder, DateTimeFormatter dateFormatter) {
    return gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonAdapter(dateFormatter));
  }

  public LocalDateGsonAdapter(DateTimeFormatter dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    return LocalDate.parse(in.nextString(), dateFormat);
  }

  @Override
  public void write(JsonWriter out, LocalDate d) throws IOException {
    out.jsonValue(d.format(dateFormat));
  }
}
