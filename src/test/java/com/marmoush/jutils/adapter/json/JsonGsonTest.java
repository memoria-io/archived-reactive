package com.marmoush.jutils.adapter.json;

import com.google.gson.Gson;
import com.marmoush.jutils.domain.port.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.marmoush.jutils.utils.file.FileUtils.resource;

public class JsonGsonTest {
  private Json json = new JsonGson(new Gson());

  @Test
  public void toMap() {
    String s = resource("json/gson-test.json").get();
    Map<String, Object> map = json.toMap(s).get();
    Assertions.assertEquals(map.get("name"), "Bob");
    Assertions.assertEquals(map.get("age"), 23.0);
    Assertions.assertEquals(map.get("cars"), List.of("mercedes", "chevy", "porsche"));
  }
}
