package io.memoria.jutils.adapter.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import io.memoria.jutils.core.dto.DTO;
import io.memoria.jutils.core.yaml.Yaml;
import io.vavr.control.Try;

import java.io.StringWriter;
import java.lang.reflect.Type;

public record YamlBeans(boolean ignoreUnknown) implements Yaml {

  @Override
  public <T> Try<T> deserialize(String str, Type type) {

    return null;
  }

  @Override
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> yamlReader(str).read(tClass));
  }

  @Override
  public <T> Try<T> deserializeByDTO(String str, Class<? extends DTO<T>> tClass) {
    return null;
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> {
      var writer = new StringWriter();
      var yw = new YamlWriter(writer);
      yw.write(t);
      return writer.toString();
    });
  }

  private YamlReader yamlReader(String s) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    return new YamlReader(s, yc);
  }
}
