package com.file.management.system.nats.transport;

import com.file.management.system.nats.transport.domain.Request;
import com.file.management.system.nats.transport.domain.Response;

@FunctionalInterface
public interface NatsMessageHandler<T, R> {

    Response<T> handleMessage(Request<R> request);

}
