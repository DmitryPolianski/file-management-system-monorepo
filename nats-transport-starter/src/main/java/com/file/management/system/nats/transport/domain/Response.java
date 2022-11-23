package com.file.management.system.nats.transport.domain;

public record Response<T>(T body, Integer statusCode) {
}
