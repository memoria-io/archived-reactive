package io.memoria.jutils.adapter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeAdapter extends TypeAdapter<LocalDateTime> {

  private final DateTimeFormatter dateTimeFormat;
  private final ZoneOffset zoneOffset;

  public DateTimeAdapter(DateTimeFormatter dateTimeFormat, ZoneOffset zoneOffset) {
    this.dateTimeFormat = dateTimeFormat;
    this.zoneOffset = zoneOffset;
  }

  @Override
  public LocalDateTime read(JsonReader in) throws IOException {
    return LocalDateTime.parse(in.nextString(), dateTimeFormat).atOffset(zoneOffset).toLocalDateTime();
  }

  @Override
  public void write(JsonWriter out, LocalDateTime localDateTime) throws IOException {
    out.jsonValue(localDateTime.atOffset(zoneOffset).format(dateTimeFormat));
  }
}
