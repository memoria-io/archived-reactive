package io.memoria.jutils.core.transformer.oas;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Oas {
  record OasEnum(String... values) {}

  record OasString(Optional<OasStringFormat> format,
                   Optional<Integer> minLength,
                   Optional<Integer> maxLength,
                   Optional<OasEnum> enumValues) implements Oas {}

  record OasArray(Optional<Integer> minItems, Optional<Integer> maxItems, Optional<Boolean> uniqueItems)
          implements Oas {}

  record OasObject(Map<String, Oas> properties, List<String> required) implements Oas {}

  record OasOneOf() implements Oas {}
  record OasAllOf()implements Oas{}
}
