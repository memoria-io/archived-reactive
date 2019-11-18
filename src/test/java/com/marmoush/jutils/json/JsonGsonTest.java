package com.marmoush.jutils.json;

import com.google.gson.Gson;
import com.marmoush.jutils.file.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.marmoush.jutils.file.FileUtils.resource;

public class JsonGsonTest {
  private Json json = new JsonGson(new Gson());

  @Test
  public void toMap() {
    String s = FileUtils.asStringBlocking(resource("json/gson-test.json"));
    Map<String, Object> map = json.toMap(s).get();
    Assertions.assertEquals(map.get("name"), "Bob");
    Assertions.assertEquals(map.get("age"), 23.0);
    Assertions.assertEquals(map.get("cars"), List.of("mercedes", "chevy", "porsche"));
  }
}
