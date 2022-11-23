package com.file.management.system.user.api.nats

import com.file.management.system.nats.transport.SyncNatsTransport
import com.file.management.system.nats.transport.domain.Request
import com.file.management.system.user.AbstractIntegrationSpec
import com.file.management.system.user.domain.view.UserView
import com.file.management.system.user.repository.UserRepository
import com.file.management.system.user.util.TestDataGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class UserHandlerIntegrationSpec extends AbstractIntegrationSpec {

    @Value('${users.endpoint.nats.create-user}')
    String createUserRoute

    @Autowired
    UserRepository userRepository

    @Autowired
    SyncNatsTransport syncNatsTransport

    def 'create user request'() {
        given: 'create user request'
        def request = TestDataGenerator.generateCreateUserRequest()

        when: 'send create user request'
        def response = syncNatsTransport.send(createUserRoute, new Request<>(request), UserView)

        then: 'response statusCode 200'
        response.statusCode() == 200

        and: 'user view with valid data'
        def userView = response.body()
        userView.id() != null
        userView.firstName() == request.firstName
        userView.lastName() == request.lastName
        userView.dateOfBirth() == request.dateOfBirth
        userView.email() == request.email

        and: 'user exist in db'
        userRepository.findById(userView.id()).isPresent()
    }

    def 'create user request with not valid email'() {
        given: 'create user request'
        def request = TestDataGenerator.generateCreateUserRequest()
        request.setEmail("bot valid email")

        when: 'send create user request'
        def response = syncNatsTransport.send(createUserRoute, new Request<>(request), UserView)

        then: 'response statusCode 400'
        response.statusCode() == 400

        and: 'body is null'
        response.body() == null
    }

    def 'create user request with null password'() {
        given: 'create user request'
        def request = TestDataGenerator.generateCreateUserRequest()
        request.setPassword(null)

        when: 'send create user request'
        def response = syncNatsTransport.send(createUserRoute, new Request<>(request), UserView)

        then: 'response statusCode 400'
        response.statusCode() == 400

        and: 'body is null'
        response.body() == null
    }

}
