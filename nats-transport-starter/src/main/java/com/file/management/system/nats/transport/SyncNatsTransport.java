package com.file.management.system.nats.transport;

import com.file.management.system.nats.transport.domain.Request;
import com.file.management.system.nats.transport.domain.Response;

public interface SyncNatsTransport {

    <R, T> Response<T> send(String route, Request<R> request, Class<T> responseClass) throws InterruptedException;

    <T, R> void subscribe(String topic, NatsMessageHandler<T, R> handler, Class<R> requestClass);

}
