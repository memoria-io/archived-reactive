[![Build](https://github.com/memoria-io/jutils/workflows/Build/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3Abuild)
[![Publish](https://github.com/memoria-io/jutils/workflows/Publish/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3APublish)
[![Codecove](https://codecov.io/github/memoria-io/jutils/coverage.svg?precision=2)](https://codecov.io/gh/memoria-io/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_jutils&metric=coverage)](https://sonarcloud.io/dashboard?id=memoria-io_jutils)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_jutils&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=memoria-io_jutils)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_jutils&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=memoria-io_jutils)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_jutils&metric=security_rating)](https://sonarcloud.io/dashboard?id=memoria-io_jutils)

# JUtils

`jutils` is a tool-box that makes it easier and faster to build reactive applications. It relies heavily
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
* jutils is up to date with the latest versions of JDK preview feature, currently `Java 15`.

## Usage

All modules depend on Core. There are currently no other inter-dependencies between them.

**Disclaimer:**
> `jutils` is on edge, it's a work in progress and a pragmatic learning effort, so feel free to create issues or PRs.

```xml

<properties>
    <jutils.version>0.9.82</jutils.version>
</properties>

<dependencies>
<dependency>
    <groupId>io.memoria.jutils</groupId>
    <artifactId>core</artifactId>
    <version>${jutils.version}</version>
</dependency>

<dependency>
    <!-- replace module_name with your preferred module -->
    <groupId>io.memoria.jutils</groupId>
    <artifactId>module_name</artifactId>
    <version>${jutils.version}</version>
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
* Pulsar as Eventstore
* Kafka as Eventstore  
* Eventstore testing suite