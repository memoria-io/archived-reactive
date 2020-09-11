package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static io.memoria.jutils.core.file.FileReader.resourcePath;

public class JsonGsonTest {
  private final Json json = new JsonGson(new Gson());

  @Test
  public void toList() {
    var list = Tests.FILE_READER.file(resourcePath("json/gsonList.json").get())
                                .map(s -> json.<List<String>>deserialize(s).get());
    StepVerifier.create(list).assertNext(m -> {
      Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), m);
    }).expectComplete().verify();
  }

  @Test
  public void toMap() {
    var map = Tests.FILE_READER.file(resourcePath("json/gsonMap.json").get())
                               .map(s -> json.<Map<String, Object>>deserialize(s).get());
    StepVerifier.create(map).assertNext(m -> {
      Assertions.assertEquals("Bob", m.get("name"));
      Assertions.assertEquals(23.0, m.get("age"));
      Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), m.get("cars"));
    }).expectComplete().verify();
  }
}
