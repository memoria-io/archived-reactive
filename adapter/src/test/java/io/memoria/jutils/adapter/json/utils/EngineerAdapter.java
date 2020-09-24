package io.memoria.jutils.adapter.json.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.memoria.jutils.adapter.json.JsonGsonUtils;
import io.memoria.jutils.adapter.json.utils.Employee.Engineer;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.io.IOException;

import static io.memoria.jutils.adapter.json.JsonException.notFound;
import static io.memoria.jutils.adapter.json.JsonGsonUtils.deserialize;

public class EngineerAdapter extends TypeAdapter<Engineer> {
  @Override
  public Engineer read(JsonReader in) throws IOException {
    in.beginObject();
    Try<String> name = Try.failure(notFound("name"));
    List<String> tasks = List.empty();
    while (in.hasNext()) {
      var nextName = in.nextName();
      switch (nextName) {
        case "name" -> name = Try.success(in.nextString());
        case "tasks" -> tasks = JsonGsonUtils.deserialize(in, JsonReader::nextString).get();
      }
    }
    in.endObject();
    return new Engineer(name.get(), tasks);
  }

  @Override
  public void write(JsonWriter out, Engineer eng) throws IOException {
    out.beginObject();
    out.name("name");
    out.value(eng.name());
    out.name("tasks");
    JsonGsonUtils.serialize(out, eng.tasks());
    out.endObject();
  }
}
