package io.memoria.jutils.adapter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeAdapter extends TypeAdapter<LocalTime> {

  private final DateTimeFormatter dateTimeFormat;
  private final ZoneOffset zoneOffset;

  public TimeAdapter(DateTimeFormatter dateTimeFormat, ZoneOffset zoneOffset) {
    this.dateTimeFormat = dateTimeFormat;
    this.zoneOffset = zoneOffset;
  }

  @Override
  public LocalTime read(JsonReader in) throws IOException {
    return LocalTime.parse(in.nextString(), dateTimeFormat).atOffset(zoneOffset).toLocalTime();
  }

  @Override
  public void write(JsonWriter out, LocalTime d) throws IOException {
    out.jsonValue(d.atOffset(zoneOffset).format(dateTimeFormat));
  }
}
