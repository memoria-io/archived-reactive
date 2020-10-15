package io.memoria.jutils.adapter.transformer;

import io.vavr.collection.List;

public interface Employee {
  record Engineer(String name, List<String> tasks) implements Employee {}

  record Manager(String name, List<Engineer> team) implements Employee {}

  String name();
}
