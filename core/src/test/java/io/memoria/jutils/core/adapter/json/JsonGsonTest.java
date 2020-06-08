package io.memoria.jutils.core.adapter.json;

import com.google.gson.Gson;
import io.memoria.jutils.core.domain.port.Json;
import io.memoria.jutils.core.utils.file.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class JsonGsonTest {
  private Json json = new JsonGson(new Gson());

  @Test
  public void toMap() {
    String s = FileUtils.resource("json/gson-test.json").get();
    Map<String, Object> map = json.toMap(s).get();
    Assertions.assertEquals("Bob", map.get("name"));
    Assertions.assertEquals(23.0, map.get("age"));
    Assertions.assertEquals(List.of("mercedes", "chevy", "porsche"), map.get("cars"));
  }
}
