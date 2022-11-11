package com.file.management.system.user.service

import com.file.management.system.user.AbstractIntegrationSpec
import com.file.management.system.user.domain.request.CreateUserRequest
import com.file.management.system.user.exception.NotValidUserDataException
import com.file.management.system.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class UserServiceSpec extends AbstractIntegrationSpec {

    @Autowired
    UserService userService

    @Autowired
    UserRepository userRepository

    def 'create user with null request'() {
        when: 'create user with null request'
        userService.create(null)

        then: 'NotValidUserData exception thrown'
        thrown NotValidUserDataException
    }

    def 'create user'() {
        given: 'create user request'
        def request = CreateUserRequest.builder()
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@email.test")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .password("testPassword")
                .build()

        when: 'create user from request'
        def response = userService.create(request)

        then: 'user saved in DB'
        response != null
        response.id != null
        def savedUserOpt = userRepository.findById(response.id)
        savedUserOpt.isPresent()

        and: 'user has equals data'
        def user = savedUserOpt.get()
        user.firstName == request.firstName
        user.lastName == request.lastName
        user.email == request.email
        user.dateOfBirth == request.dateOfBirth
    }

}
