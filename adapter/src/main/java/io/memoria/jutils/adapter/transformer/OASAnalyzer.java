package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.oas.Pojo;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoArray;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoBoolean;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoDouble;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoFloat;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoInteger;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoLong;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoMap;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoObject;
import io.memoria.jutils.core.transformer.oas.Pojo.PojoString;
import io.vavr.control.Option;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class OASAnalyzer {
  public static <T> Pojo analyse(Class<T> c) {
    return asPrimitive(c).orElse(() -> asStandardObject(c))
                         .orElse(() -> asArray(c))
                         .getOrElse(() -> asUserDefinedObject(c));
  }

  public static <T> Pojo asUserDefinedObject(Class<T> c) {
    var map = new HashMap<String, Pojo>();
    var fields = (c.isRecord()) ? c.getDeclaredFields() : c.getFields();
    // TODO annotated public methods
    for (Field f : fields) {
      var oas = asJavaCollection(f).orElse(() -> asMap(f)).orElse(asGeneric(f)).getOrElse(analyse(f.getType()));
      map.put(f.getName(), oas);
    }
    return new PojoObject(map);
  }

  public static Option<Pojo> asGeneric(Field f) {
    if (f.getGenericType() instanceof ParameterizedType p) {
      return Option.some(fromGeneric(p, f.getType()));
    }
    return Option.none();
  }

  public static <T> Pojo fromGeneric(ParameterizedType parameterizedType, Class<T> rawType) {
    // Get Parameter names in code
    var params = rawType.getTypeParameters();
    var paramsNames = Arrays.stream(params).map(TypeVariable::getName).collect(toSet());
    // Match types with names
    var nameAndType = new HashMap<String, Type>();
    var typeArguments = parameterizedType.getActualTypeArguments();
    for (int i = 0; i < typeArguments.length; i++) {
      nameAndType.put(params[i].getName(), typeArguments[i]);
    }
    var objectMap = new HashMap<String, Pojo>();
    var fields = (rawType.isRecord()) ? rawType.getDeclaredFields() : rawType.getFields();
    for (Field field : fields) {
      if (field.getType().equals(Object.class) && paramsNames.contains(field.getGenericType().getTypeName())) {
        var t = nameAndType.get(field.getGenericType().getTypeName());
        if (t instanceof Class<?> classT) {
          objectMap.put(field.getName(), analyse(classT));
        } else if (t instanceof ParameterizedType pt) {
          var oas = fromGeneric(pt, (Class<?>) pt.getRawType());
          objectMap.put(field.getName(), oas);
        }
      } else {
        objectMap.put(field.getName(), analyse(field.getType()));
      }
    }
    return new PojoObject(objectMap);
  }

  public static Option<Pojo> asMap(Field f) {
    if (Map.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var valueClass = (Class<?>) type.getActualTypeArguments()[1];
      return Option.some(new PojoMap(analyse(valueClass)));
    }
    return Option.none();
  }

  public static Option<Pojo> asJavaCollection(Field f) {
    if (Collection.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var cls = (Class<?>) type.getActualTypeArguments()[0];
      return Option.some(new PojoArray(analyse(cls)));
    }
    return Option.none();
  }

  public static <T> Option<Pojo> asArray(Class<T> c) {
    if (c.isArray()) {
      var arrayType = c.getComponentType();
      var arrayOf = analyse(arrayType);
      return Option.some(new PojoArray(arrayOf));
    }
    return Option.none();
  }

  public static <T> Option<Pojo> asStandardObject(Class<T> c) {
    if (c.equals(String.class))
      return Option.some(new PojoString());
    if (c.equals(Boolean.class))
      return Option.some(new PojoBoolean());
    if (c.equals(Character.class) || c.equals(Byte.class))
      return Option.some(new PojoString());
    if (c.equals(Short.class) || c.equals(Integer.class))
      return Option.some(new PojoInteger());
    if (c.equals(Long.class))
      return Option.some(new PojoLong());
    if (c.equals(Float.class))
      return Option.some(new PojoFloat());
    if (c.equals(Double.class))
      return Option.some(new PojoDouble());
    return Option.none();
  }

  public static <T> Option<Pojo> asPrimitive(Class<T> c) {
    if (c.isPrimitive()) {
      if (c.equals(boolean.class))
        return Option.some(new PojoBoolean());
      if (c.equals(char.class) || c.equals(byte.class))
        return Option.some(new PojoString());
      if (c.equals(short.class) || c.equals(int.class))
        return Option.some(new PojoInteger());
      if (c.equals(long.class))
        return Option.some(new PojoLong());
      if (c.equals(float.class))
        return Option.some(new PojoFloat());
      if (c.equals(double.class))
        return Option.some(new PojoDouble());
    }
    return Option.none();
  }

  private OASAnalyzer() {}
}
