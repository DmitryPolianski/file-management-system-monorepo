package com.file.management.system.nats.transport.exception;

public class NatsTransportException extends RuntimeException {

    public NatsTransportException(String message) {
        super(message);
    }

    public NatsTransportException(String message, Throwable cause) {
        super(message, cause);
    }

}
