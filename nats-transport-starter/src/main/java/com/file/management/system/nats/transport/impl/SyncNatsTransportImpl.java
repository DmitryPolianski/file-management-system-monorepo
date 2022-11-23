package com.file.management.system.nats.transport.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.file.management.system.nats.transport.NatsMessageHandler;
import com.file.management.system.nats.transport.SyncNatsTransport;
import com.file.management.system.nats.transport.domain.Request;
import com.file.management.system.nats.transport.domain.Response;
import com.file.management.system.nats.transport.exception.NatsTransportException;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncNatsTransportImpl implements SyncNatsTransport {

    @Value("${spring.nats.request.timeout-in-seconds}")
    private long timeoutInSeconds;

    @Value("${spring.nats.queue}")
    private String queueGroup;

    private final Connection nats;
    private final ObjectMapper objectMapper;

    @Override
    public <R, T> Response<T> send(String route, Request<R> request, Class<T> responseClass) throws InterruptedException {
        try {
            var response = nats.request(route, writeAsBytes(request), Duration.ofSeconds(timeoutInSeconds));
            if (response != null) {
                return readResponse(response.getData(), responseClass);
            } else {
                throw new NatsTransportException("Request failed due to timeout");
            }
        } catch (NatsTransportException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new NatsTransportException("An error occurred while sending the message", ex);
        }
    }

    @Override
    public <T, R> void subscribe(String topic, NatsMessageHandler<T, R> handler, Class<R> requestClass) {
        MessageHandler messageHandler = (message) -> {
            Request<R> request = readRequest(message.getData(), requestClass);
            Response<T> response = handler.handleMessage(request);
            nats.publish(message.getReplyTo(), writeAsBytes(response));
        };

        Dispatcher dispatcher = nats.createDispatcher(messageHandler);
        dispatcher.subscribe(topic, queueGroup);
    }

    private <R> byte[] writeAsBytes(R request) {
        try {
            return objectMapper.writeValueAsBytes(request);
        } catch (JsonProcessingException ex) {
            throw new NatsTransportException("Unable to write as bytes", ex);
        }
    }

    private <T> Response<T> readResponse(byte[] byteArray, Class<T> valueClass) {
        try {
            var map = objectMapper.readValue(byteArray, Map.class);
            var body = objectMapper.convertValue(map.get("body"), valueClass);
            var statusCode = objectMapper.convertValue(map.get("statusCode"), Integer.class);
            return new Response<>(body, statusCode);
        } catch (IOException ex) {
            throw new NatsTransportException("Unable to read response data", ex);
        }
    }

    private <R> Request<R> readRequest(byte[] byteArray, Class<R> requestClass) {
        try {
            var map = objectMapper.readValue(byteArray, Map.class);
            var body = objectMapper.convertValue(map.get("body"), requestClass);
            return new Request<>(body);
        } catch (IOException ex) {
            throw new NatsTransportException("Unable to read request data", ex);
        }
    }

}
