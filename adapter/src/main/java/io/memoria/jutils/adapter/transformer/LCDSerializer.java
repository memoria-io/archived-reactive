package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.lcd.LCD;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDArray;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDBoolean;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDDouble;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDFloat;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDInteger;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDLong;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDMap;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDObject;
import io.memoria.jutils.core.transformer.lcd.LCD.LCDString;
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

public class LCDSerializer {
  public static <T> LCD serialize(Class<T> c) {
    return asPrimitive(c).orElse(() -> asStandardObject(c))
                         .orElse(() -> asArray(c))
                         .getOrElse(() -> asUserDefinedObject(c));
  }

  public static <T> LCD asUserDefinedObject(Class<T> c) {
    var map = new HashMap<String, LCD>();
    var fields = (c.isRecord()) ? c.getDeclaredFields() : c.getFields();
    // TODO annotated public methods
    for (Field f : fields) {
      var oas = asJavaCollection(f).orElse(() -> asMap(f)).orElse(asGeneric(f)).getOrElse(serialize(f.getType()));
      map.put(f.getName(), oas);
    }
    return new LCDObject(map);
  }

  public static Option<LCD> asGeneric(Field f) {
    if (f.getGenericType() instanceof ParameterizedType p) {
      return Option.some(fromGeneric(p, f.getType()));
    }
    return Option.none();
  }

  public static <T> LCD fromGeneric(ParameterizedType parameterizedType, Class<T> rawType) {
    // Get Parameter names in code
    var params = rawType.getTypeParameters();
    var paramsNames = Arrays.stream(params).map(TypeVariable::getName).collect(toSet());
    // Match types with names
    var nameAndType = new HashMap<String, Type>();
    var typeArguments = parameterizedType.getActualTypeArguments();
    for (int i = 0; i < typeArguments.length; i++) {
      nameAndType.put(params[i].getName(), typeArguments[i]);
    }
    var objectMap = new HashMap<String, LCD>();
    var fields = (rawType.isRecord()) ? rawType.getDeclaredFields() : rawType.getFields();
    for (Field field : fields) {
      if (field.getType().equals(Object.class) && paramsNames.contains(field.getGenericType().getTypeName())) {
        var t = nameAndType.get(field.getGenericType().getTypeName());
        if (t instanceof Class<?> classT) {
          objectMap.put(field.getName(), serialize(classT));
        } else if (t instanceof ParameterizedType pt) {
          var oas = fromGeneric(pt, (Class<?>) pt.getRawType());
          objectMap.put(field.getName(), oas);
        }
      } else {
        objectMap.put(field.getName(), serialize(field.getType()));
      }
    }
    return new LCDObject(objectMap);
  }

  public static Option<LCD> asMap(Field f) {
    if (Map.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var valueClass = (Class<?>) type.getActualTypeArguments()[1];
      return Option.some(new LCDMap(serialize(valueClass)));
    }
    return Option.none();
  }

  public static Option<LCD> asJavaCollection(Field f) {
    if (Collection.class.isAssignableFrom(f.getType())) {
      ParameterizedType type = (ParameterizedType) f.getGenericType();
      var cls = (Class<?>) type.getActualTypeArguments()[0];
      return Option.some(new LCDArray(serialize(cls)));
    }
    return Option.none();
  }

  public static <T> Option<LCD> asArray(Class<T> c) {
    if (c.isArray()) {
      var arrayType = c.getComponentType();
      var arrayOf = serialize(arrayType);
      return Option.some(new LCDArray(arrayOf));
    }
    return Option.none();
  }

  public static <T> Option<LCD> asStandardObject(Class<T> c) {
    if (c.equals(String.class))
      return Option.some(new LCDString());
    if (c.equals(Boolean.class))
      return Option.some(new LCDBoolean());
    if (c.equals(Character.class) || c.equals(Byte.class))
      return Option.some(new LCDString());
    if (c.equals(Short.class) || c.equals(Integer.class))
      return Option.some(new LCDInteger());
    if (c.equals(Long.class))
      return Option.some(new LCDLong());
    if (c.equals(Float.class))
      return Option.some(new LCDFloat());
    if (c.equals(Double.class))
      return Option.some(new LCDDouble());
    return Option.none();
  }

  public static <T> Option<LCD> asPrimitive(Class<T> c) {
    if (c.isPrimitive()) {
      if (c.equals(boolean.class))
        return Option.some(new LCDBoolean());
      if (c.equals(char.class) || c.equals(byte.class))
        return Option.some(new LCDString());
      if (c.equals(short.class) || c.equals(int.class))
        return Option.some(new LCDInteger());
      if (c.equals(long.class))
        return Option.some(new LCDLong());
      if (c.equals(float.class))
        return Option.some(new LCDFloat());
      if (c.equals(double.class))
        return Option.some(new LCDDouble());
    }
    return Option.none();
  }

  private LCDSerializer() {}
}
