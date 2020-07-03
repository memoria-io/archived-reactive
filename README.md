
[![Build](https://github.com/memoria-io/jutils/workflows/Build/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3Abuild)
[![Publish](https://github.com/memoria-io/jutils/workflows/Publish/badge.svg)](https://github.com/memoria-io/jutils/actions?query=workflow%3APublish)
[![Codecove](https://codecov.io/github/memoria-io/jutils/coverage.svg?precision=2)](https://codecov.io/gh/memoria-io/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.memoria/jutils/)

# jutils
`jutils` is a reactive functional utilities' library, an opinionated and pragmatic learning effort.
It was created because I found much reused code after combining multiple libraries;

One example is the combination of [Project Reactor](https://projectreactor.io/) along with [VAVR](https://vavr.io), it
became clear that there was a need for functional utility methods like the ones in
[functional](core/src/main/java/io/memoria/jutils/core/utils/functional) package.

**Why I moved Jutils from my own github account to become under Memoria ?**
> I believe in the power of community and ownership, so this step was taken encourage people to contribute and be part of something 
> they feel they own, and that their contributions are not wasted to an individual but to a team they're part of.

**About Jutils CI/CD:**

Jutils refactoring and updates is very fast, thanks to Github actions and before that was Travis, 
I worked hard to maintain a 5~minute full pipeline cycle - from code to Maven release to the public.
A bar of >90% test coverage would hopefully become stable soon, to allow much safer refactoring efforts.


## Features
* Vavr + Reactor Functional utilities
* Yaml configuration loader utility (Currently is very tiny utility but hopefully soon to have features like like Scala's HOCON )
    * Currently, it's non-blocking wrapped in Reactor Mono/Flux
    * It has nesting capability using  `include: file.yaml` 
* Reactive CQRS and Eventsourcing utilities.
* Simple Reactive Message bus implementation for:
  * Apache Kafka
  * Nats
  * Apache Pulsar
* Reactor Netty adapter with error handling escalation
* Vavr + GSON Json implementation
* Nonblocking Argon2 Hashing adapter
* Reactive functional in-memory generic cruds.

**Disclaimer:**

> Jutils is never perfect and there might be critical mistakes/bugs there, use it with caution, and I'm very flexible 
> when it comes to fixing wrong things even if the effort was big, I accept issues, and welcome PRs, 

> There will be much better care for breaking changes, soon as the library gets more users (stars) and traffic, and before 
> that when the library itself become more stable and ideas turn into solid implementations. 


## Usage
All modules depend on core. There are currently no other inter-dependencies between them.

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
        <artifactId> module_name </artifactId>
        <version>${jutils.version}</version>
    </dependency>
</dependencies>
```

## Related Articles
* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)

## Contribution
You can just do pull requests, and I will check them asap.
