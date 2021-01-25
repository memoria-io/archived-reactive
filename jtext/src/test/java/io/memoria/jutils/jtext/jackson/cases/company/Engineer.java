package io.memoria.jutils.jtext.jackson.cases.company;

import io.vavr.collection.List;

import java.time.LocalDate;

public record Engineer(String name, LocalDate birthday, List<String> tasks) implements Employee {}
