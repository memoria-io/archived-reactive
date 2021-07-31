package io.memoria.reactive.text.jackson.cases.company;

import io.vavr.collection.List;

public record Department(List<Employee> employees) {}
