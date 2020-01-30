package com.marmoush.jutils.core.adapter.json;

import com.google.gson.Gson;
import com.marmoush.jutils.core.domain.port.Json;
import org.junit.jupiter.api.*;

import java.util.*;

import static com.marmoush.jutils.core.utils.file.FileUtils.resource;

public class JsonGsonTest {
  private Json json = new JsonGson(new Gson());

  @Test
  public void toMap() {
    String s = resource("json/gson-test.json").get();
    Map<String, Object> map = json.toMap(s).get();
    Assertions.assertEquals("Bob", map.get("name"));
    Assertions.assertEquals(23.0, map.get("age"));
    Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), map.get("cars"));
  }
}
