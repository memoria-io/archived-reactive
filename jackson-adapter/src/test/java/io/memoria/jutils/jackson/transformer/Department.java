package io.memoria.jutils.jackson.transformer;

import io.vavr.collection.List;

record Department(List<Employee> persons) {}
