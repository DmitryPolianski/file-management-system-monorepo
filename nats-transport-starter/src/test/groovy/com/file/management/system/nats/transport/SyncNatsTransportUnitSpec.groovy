package com.file.management.system.nats.transport

import com.fasterxml.jackson.databind.ObjectMapper
import com.file.management.system.nats.transport.domain.Request
import com.file.management.system.nats.transport.domain.Response
import com.file.management.system.nats.transport.exception.NatsTransportException
import com.file.management.system.nats.transport.impl.SyncNatsTransportImpl
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import io.nats.client.impl.NatsMessage
import spock.lang.Specification

import java.time.Duration

class SyncNatsTransportUnitSpec extends Specification {

    private SyncNatsTransportImpl syncNatsTransport
    private Connection connection = Mock()
    private ObjectMapper objectMapper

    def setup() {
        objectMapper = new ObjectMapper()
        syncNatsTransport = new SyncNatsTransportImpl(connection, objectMapper)
        syncNatsTransport.timeoutInSeconds = 10
    }

    def 'Send should return response'() {
        given: 'request'
        def request = new Request("testRequest")
        connection.request(_ as String, _ as byte[], _ as Duration) >>
                (String topic, byte[] req, Duration duration) -> {
                    def response = objectMapper.writeValueAsBytes(new Response("testResponse", 200))
                    return new NatsMessage(topic, "someReplayTo", response)
                }

        when: 'send request'
        def response = syncNatsTransport.send("test", request, String.class)

        then: 'check result'
        response.statusCode() == 200
        response.body() == "testResponse"
    }

    def 'Send when request return null response should throw NatsTransportException'() {
        given: 'request'
        def request = new Request("testRequest")
        connection.request(_ as String, _ as byte[], _ as Duration) >> null

        when: 'send request'
        syncNatsTransport.send("test", request, String.class)

        then: 'check result'
        thrown NatsTransportException
    }

    def 'Send when request throw runtime exception should throw NatsTransportException'() {
        given: 'request'
        def request = new Request("testRequest")
        connection.request(_ as String, _ as byte[], _ as Duration) >> { throw new RuntimeException() }

        when: 'send request'
        syncNatsTransport.send("test", request, String.class)

        then: 'check result'
        thrown NatsTransportException
    }

    def 'subscribe should subscribe for specific topic'() {
        given: 'handler'
        Dispatcher dispatcher = Mock(Dispatcher)
        NatsMessageHandler handler = (Request<String> request) -> new Response<String>(request.body(), 200)

        when: 'subscribe'
        syncNatsTransport.subscribe("test", handler, String)

        then: 'should create dispatcher'
        1 * connection.createDispatcher(_ as MessageHandler) >> {
            arguments ->
                {
                    def request = objectMapper.writeValueAsBytes(new Request("testResponse"))
                    def message = new NatsMessage("topic", "someReplayTo", request)
                    ((MessageHandler) arguments[0]).onMessage(message)

                    return dispatcher
                }
        }
        and: 'should subscribe'
        1 * dispatcher.subscribe("test", _)
    }

}
