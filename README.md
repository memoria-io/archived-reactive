[![Release](https://github.com/memoria-io/reactive/workflows/Release/badge.svg)](https://github.com/memoria-io/reactive/actions?query=workflow%3ARelease)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/memoria-io/reactive?label=Version&logo=github)](https://github.com/orgs/memoria-io/packages?repo_name=reactive)
[![GitHub commits since latest release (by date)](https://img.shields.io/github/commits-since/memoria-io/reactive/latest?logoColor=github)](https://github.com/memoria-io/reactive/commits/master)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_reactive&metric=coverage)](https://sonarcloud.io/dashboard?id=memoria-io_reactive)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_reactive&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=memoria-io_reactive)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_reactive&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=memoria-io_reactive)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_reactive&metric=security_rating)](https://sonarcloud.io/dashboard?id=memoria-io_reactive)

# reactive

> هذا العلم والعمل وقف للّه تعالي اسأل اللّه ان يرزقنا الاخلاص فالقول والعمل
>
> This work and knowledge is for the sake of Allah, may Allah grant us sincerity in what we say or do.

`reactive` is a tool-box that makes it easier and faster to build reactive applications. It relies heavily
on [Reactive Streams](https://www.reactive-streams.org/) and uses [Project-Reactor](https://projectreactor.io/),
[Reactor Netty](https://github.com/reactor/reactor-netty), it also uses functional paradigms and collections
from [Vavr](https://www.vavr.io/),

## Features

* Vavr + Reactor Functional utilities
* Reactor Netty utility functions
* Reactive CQRS and Eventsourcing utilities (alpha stage)
* Reactive functional in-memory generic cruds (for tests, not for production)
* Jackson Adapter (Json & Yaml) utilities
* File reader utility
* jconf is a module for reading yaml configuration files (depends on Jackson Adapter)
    * Allows nesting of files using a marker e.g `include: sub_file.yaml` would replace this line with content
      of `sub_file.yaml`
    * Reading as a system property if not found as environment variable or else the default value if it was supplied:
        * `path: ${JAVA_HOME}`
        * `myVar: ${SOME_VAR:-defaultValue}`
* reactive is up to date with the latest versions of JDK preview feature, currently `Java 15`.

## Usage

All modules depend on Core. There are currently no other inter-dependencies between them.

**Disclaimer:**
> `reactive` is on edge, it's a work in progress and a pragmatic learning effort, so feel free to create issues or PRs.

```xml

<properties>
    <reactive.version>0.9.82</reactive.version>
</properties>

<dependencies>
<dependency>
    <groupId>io.memoria.reactive</groupId>
    <artifactId>core</artifactId>
    <version>${reactive.version}</version>
</dependency>

<dependency>
    <!-- replace module_name with your preferred module -->
    <groupId>io.memoria.reactive</groupId>
    <artifactId>module_name</artifactId>
    <version>${reactive.version}</version>
</dependency>
</dependencies>
```

## Related Articles

* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)

## Contribution

You can just do pull requests, and I will check them asap.

## TODOs

* CommandHandler with eventbus as eventstore
* Pulsar as streaming bus
* Eventstore testing suite