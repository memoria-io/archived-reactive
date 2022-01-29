package io.memoria.reactive.web.netty;

import io.netty.handler.codec.http.HttpResponseStatus;

record DefaultResponse(HttpResponseStatus status, String payload) implements Response {}
