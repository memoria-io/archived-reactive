package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import io.memoria.jutils.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static io.memoria.jutils.core.file.FileReader.resourcePath;

public class JsonGsonTest {
  private final Json json = new JsonGson(new Gson());

  @Test
  public void toMap() {
    var map = Tests.FILE_READER.file(resourcePath("json/gson-test.json").get()).map(s -> json.toMap(s).get());
    StepVerifier.create(map).assertNext(m -> {
      Assertions.assertEquals("Bob", m.get("name"));
      Assertions.assertEquals(23.0, m.get("age"));
      Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), m.get("cars"));
    }).expectComplete().verify();
  }
}
