package io.memoria.reactive.text.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.text.jackson.adapters.IdTransformer.IdDeserializer;
import io.memoria.reactive.text.jackson.adapters.IdTransformer.IdSerializer;
import io.vavr.jackson.datatype.VavrModule;

import java.text.SimpleDateFormat;

public class JacksonUtils {
  private JacksonUtils() {}

  public static ObjectMapper addJ8Modules(ObjectMapper om) {
    return om.registerModule(new ParameterNamesModule())
             .registerModule(new Jdk8Module())
             .registerModule(new JavaTimeModule());
  }

  public static ObjectMapper addVavrModule(ObjectMapper om) {
    return om.registerModule(new VavrModule());
  }

  public static ObjectMapper json() {
    ObjectMapper om = JsonMapper.builder().build();
    om = setDateFormat(om);
    om = addJ8Modules(om);
    om = addVavrModule(om);
    om.registerModule(reactiveModule());
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return om;
  }

  public static ObjectMapper jsonPrettyPrinting(ObjectMapper om) {
    var printer = new DefaultPrettyPrinter().withoutSpacesInObjectEntries();
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    var resultMapper = om.enable(SerializationFeature.INDENT_OUTPUT);
    resultMapper.setDefaultPrettyPrinter(printer);

    return resultMapper;
  }

  /**
   * Maps inheriting classes simple names written with format "As.PROPERTY" and property name is "@type" to this
   * baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   * @return a new {@link JacksonUtils}
   */
  public static ObjectMapper mixinPropertyFormat(ObjectMapper om, Class<?>... baseClass) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      om.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return om;
  }

  /**
   * Maps inheriting classes simple names written with format "As.WRAPPER_OBJECT" to this baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   * @return a new {@link JacksonUtils}
   */
  public static ObjectMapper mixinWrapperObjectFormat(ObjectMapper om, Class<?>... baseClass) {
    @JsonTypeInfo(include = As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      om.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return om;
  }

  public static ObjectMapper prettyJson() {
    return jsonPrettyPrinting(json());
  }

  public static SimpleModule reactiveModule() {
    var reactive = new SimpleModule();
    // Id
    reactive.addSerializer(Id.class, new IdSerializer());
    reactive.addDeserializer(Id.class, new IdDeserializer());
    return reactive;
  }

  public static ObjectMapper setDateFormat(ObjectMapper om) {
    return om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd" + "'T'" + "HH:mm:ss"));
  }

  public static ObjectMapper yaml() {
    var yfb = new YAMLFactoryBuilder(YAMLFactory.builder().build());
    yfb.configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true);
    var om = new ObjectMapper(yfb.build());
    om = setDateFormat(om);
    om = addJ8Modules(om);
    om = addVavrModule(om);
    om.registerModule(reactiveModule());
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return om;
  }
}
