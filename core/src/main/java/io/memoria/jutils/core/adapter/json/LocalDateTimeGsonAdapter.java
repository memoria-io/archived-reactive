package io.memoria.jutils.core.adapter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeGsonAdapter extends TypeAdapter<LocalDateTime> {

  public static GsonBuilder register(GsonBuilder gsonBuilder, DateTimeFormatter dateFormatter, ZoneOffset zoneOffset) {
    return gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                                           new LocalDateTimeGsonAdapter(dateFormatter, zoneOffset));
  }

  private final DateTimeFormatter dateTimeFormat;
  private final ZoneOffset zoneOffset;

  public LocalDateTimeGsonAdapter(DateTimeFormatter dateTimeFormat, ZoneOffset zoneOffset) {
    this.dateTimeFormat = dateTimeFormat;
    this.zoneOffset = zoneOffset;
  }

  @Override
  public void write(JsonWriter out, LocalDateTime d) throws IOException {
    out.jsonValue(d.atOffset(zoneOffset).format(dateTimeFormat));
  }

  @Override
  public LocalDateTime read(JsonReader in) throws IOException {
    return LocalDateTime.parse(in.nextString(), dateTimeFormat).atOffset(zoneOffset).toLocalDateTime();
  }
}
