package com.marmoush.jutils.core.adapter.json;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateGsonAdapter extends TypeAdapter<LocalDate> {
  public static GsonBuilder register(GsonBuilder gsonBuilder, DateTimeFormatter dateFormatter) {
    return gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateGsonAdapter(dateFormatter));
  }

  private final DateTimeFormatter dateFormat;

  public LocalDateGsonAdapter(DateTimeFormatter dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Override
  public void write(JsonWriter out, LocalDate d) throws IOException {
    out.jsonValue(d.format(dateFormat));
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    return LocalDate.parse(in.nextString(), dateFormat);
  }
}
