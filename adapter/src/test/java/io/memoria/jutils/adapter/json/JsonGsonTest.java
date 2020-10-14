package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.adapter.transformer.json.JsonGson;
import io.memoria.jutils.core.transformer.json.Json;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.gson.VavrGson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.jutils.core.transformer.file.FileReader.resourcePath;

class JsonGsonTest {
  private final Gson gson = VavrGson.registerAll(new GsonBuilder()).create();
  private final Json json = new JsonGson(gson);

  @Test
  void toList() {
    var stringListType = new TypeToken<List<String>>() {}.getType();
    var list = Tests.FILE_READER.file(resourcePath("json/gsonList.json").get())
                                .map(s -> json.<List<String>>deserialize(s, stringListType).get());
    StepVerifier.create(list).assertNext(m -> {
      Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), m);
    }).expectComplete().verify();
  }

  @Test
  void toMap() {
    var mapType = new TypeToken<Map<String, Object>>() {}.getType();
    var map = Tests.FILE_READER.file(resourcePath("json/gsonMap.json").get())
                               .map(s -> json.<Map<String, Object>>deserialize(s, mapType).get());
    StepVerifier.create(map).assertNext(m -> {
      Assertions.assertEquals("Bob", m.get("name").get());
      Assertions.assertEquals(23.0, m.get("age").get());
      Assertions.assertEquals(java.util.List.of("mercedes", "chevy", "porsche"), m.get("cars").get());
    }).expectComplete().verify();
  }
}
