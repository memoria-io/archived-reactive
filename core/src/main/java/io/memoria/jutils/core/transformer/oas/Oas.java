package io.memoria.jutils.core.transformer.oas;

import io.memoria.jutils.core.transformer.oas.OasType.OasOneType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Oas {
  OasType type();

  record OasEnum(String... values) {}

  record OasString(Optional<OasStringFormat> format,
                   Optional<Integer> minLength,
                   Optional<Integer> maxLength,
                   Optional<OasEnum> isEnum) implements Oas {
    @Override
    public OasType type() {
      return OasOneType.STRING;
    }
  }

  record OasArray(OasType itemsType,
                  Optional<Integer> minItems,
                  Optional<Integer> maxItems,
                  Optional<Boolean> uniqueItems) implements Oas {
    @Override
    public OasType type() {
      return OasOneType.ARRAY;
    }
  }

  record OasObjectDto(Map<String, Oas> properties, List<String> required) implements Oas {
    @Override
    public OasType type() {
      return OasOneType.OBJECT;
    }
  }
}
