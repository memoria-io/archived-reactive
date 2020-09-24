package io.memoria.jutils.adapter.json.utils;

import io.vavr.collection.List;

interface Employee {
  record Engineer(String name, List<String> tasks) implements Employee {}

  record Manager(String name, List<Engineer> team) implements Employee {}

  String name();
}
