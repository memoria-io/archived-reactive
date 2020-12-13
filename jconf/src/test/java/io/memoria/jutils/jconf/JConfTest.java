package io.memoria.jutils.jconf;

import io.memoria.jutils.core.utils.file.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

class JConfTest {
  private static record App(String first, String second, int third, String invalid) {}

  private static final JConf jConf = new JConf(Schedulers.boundedElastic());

  @Test
  void property() {
    Assertions.assertEquals("${hello:-hi:-asdf", JConf.property("${hello:-hi:-asdf"));
  }

  @Test
  void readDefaults() {
    var appFromFile = jConf.read(FileUtils.resourcePath("app.yaml").get(), App.class).block();
    var appFromResource = jConf.readResource("app.yaml", App.class).block();
    assert appFromFile != null && appFromResource != null;
    Assertions.assertEquals(appFromResource, appFromFile);
    Assertions.assertEquals("defaultValue", appFromFile.first);
    Assertions.assertEquals("${second}", appFromFile.second);
    Assertions.assertEquals(500, appFromFile.third);
    Assertions.assertEquals("${heyy:--bye}", appFromFile.invalid);
  }

  @Test
  void readSystemProperty() {
    System.setProperty("FIRST_VAR", "some value");
    var app = jConf.readResource("app.yaml", App.class).block();
    assert app != null;
    Assertions.assertEquals("some value", app.first);
    Assertions.assertEquals("${second}", app.second);
    Assertions.assertEquals(500, app.third);
  }
}
