package io.memoria.jutils.core.transformer;

import java.util.Map;

public sealed interface OAS3 {
  record OAS3Array(OAS3 type) implements OAS3 {}

  record OAS3Boolean() implements OAS3 {}

  record OAS3Float() implements OAS3 {}

  record OAS3Double() implements OAS3 {}

  record OAS3Integer() implements OAS3 {}

  record OAS3Long() implements OAS3 {}

  record OAS3Object(Map<String, OAS3> properties) implements OAS3 {}

  record OAS3String() implements OAS3 {}
}
