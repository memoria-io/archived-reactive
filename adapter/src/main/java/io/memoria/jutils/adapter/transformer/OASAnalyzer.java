package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.OAS3;
import io.memoria.jutils.core.transformer.OAS3.OAS3Array;
import io.memoria.jutils.core.transformer.OAS3.OAS3Boolean;
import io.memoria.jutils.core.transformer.OAS3.OAS3Double;
import io.memoria.jutils.core.transformer.OAS3.OAS3Float;
import io.memoria.jutils.core.transformer.OAS3.OAS3Integer;
import io.memoria.jutils.core.transformer.OAS3.OAS3Long;
import io.memoria.jutils.core.transformer.OAS3.OAS3Object;
import io.memoria.jutils.core.transformer.OAS3.OAS3String;
import io.vavr.control.Option;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

public class OASAnalyzer {
  public static <T> OAS3 analyse(Class<T> c) {
    return asPrimitive(c).orElse(() -> asStandardObject(c))
                         .orElse(() -> asArray(c))
                         .getOrElse(() -> asUserDefinedObject(c));
  }

  public static <T> OAS3 asUserDefinedObject(Class<T> c) {
    var map = new HashMap<String, OAS3>();
    var fields = (c.isRecord()) ? c.getDeclaredFields() : c.getFields();
    for (Field f : fields) {
      map.put(f.getName(), analyse(f.getType()));
    }
    return new OAS3Object(map);
  }

  public static <T> Option<OAS3> asGeneric(Class<T> c) {
    return null;
  }

  public static <T> Option<OAS3> asMap(Class<T> c) {
    return null;
  }

  public static <T> Option<OAS3> asJavaCollection(Field f) {
    if (Collection.class.isAssignableFrom(f.getType())) {
      System.out.println(f.getGenericType());
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      System.out.println(type.getTypeName());
      var raw = (Class<?>) type.getRawType();
      Collection.class.isAssignableFrom(f.getType());
      var cls = (Class<?>) type.getActualTypeArguments()[0];
      System.out.println(cls);
    }
    return Option.none();
  }

  public static <T> Option<OAS3> asArray(Class<T> c) {
    if (c.isArray()) {
      var arrayType = c.getComponentType();
      var arrayOf = analyse(arrayType);
      return Option.some(new OAS3Array(arrayOf));
    }
    return Option.none();
  }

  public static <T> Option<OAS3> asStandardObject(Class<T> c) {
    if (c.equals(String.class))
      return Option.some(new OAS3String());
    if (c.equals(Boolean.class))
      return Option.some(new OAS3Boolean());
    if (c.equals(Character.class) || c.equals(Byte.class))
      return Option.some(new OAS3String());
    if (c.equals(Short.class) || c.equals(Integer.class))
      return Option.some(new OAS3Integer());
    if (c.equals(Long.class))
      return Option.some(new OAS3Long());
    if (c.equals(Float.class))
      return Option.some(new OAS3Float());
    if (c.equals(Double.class))
      return Option.some(new OAS3Double());
    return Option.none();
  }

  public static <T> Option<OAS3> asPrimitive(Class<T> c) {
    if (c.isPrimitive()) {
      if (c.equals(boolean.class))
        return Option.some(new OAS3Boolean());
      if (c.equals(char.class) || c.equals(byte.class))
        return Option.some(new OAS3String());
      if (c.equals(short.class) || c.equals(int.class))
        return Option.some(new OAS3Integer());
      if (c.equals(long.class))
        return Option.some(new OAS3Long());
      if (c.equals(float.class))
        return Option.some(new OAS3Float());
      if (c.equals(double.class))
        return Option.some(new OAS3Double());
    }
    return Option.none();
  }

  public static boolean isClass(Type type) {
    return type instanceof Class<?>;
  }

  public static boolean isGeneric(Type type) {
    return type instanceof ParameterizedType;
  }

  public static <T> boolean isGeneric(Class<T> c) {
    return c.getTypeParameters().length > 0;
  }

  private OASAnalyzer() {}
}
