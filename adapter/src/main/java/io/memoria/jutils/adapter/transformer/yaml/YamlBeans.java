package io.memoria.jutils.adapter.transformer.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.core.dto.DTO;
import io.memoria.jutils.core.transformer.yaml.Yaml;
import io.vavr.control.Try;

import java.io.StringWriter;
import java.lang.reflect.Type;

import static java.util.function.Function.identity;

public record YamlBeans(boolean ignoreUnknown) implements Yaml {
  public YamlBeans() {
    this(true);
  }

  @Override
  public <T> Try<T> deserialize(String str, Type type) {
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) TypeToken.get(type).getRawType();
    return Try.of(() -> yamlReader(str).read(clazz)).map(identity());
  }

  @Override
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> yamlReader(str).read(tClass));
  }

  @Override
  public <T> Try<T> deserializeByDTO(String str, Class<? extends DTO<T>> tClass) {
    return Try.of(() -> yamlReader(str).read(tClass).get()).flatMap(identity());
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
