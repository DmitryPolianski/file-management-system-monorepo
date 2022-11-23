package com.file.management.system.user.util

import com.file.management.system.user.domain.request.CreateUserRequest
import org.apache.commons.lang3.RandomStringUtils

import java.time.LocalDate

class TestDataGenerator {

    private static def GENERATE_EMAIL_PATTERN = "%s@generated.email"

    static def generateEmail() {
        return String.format(GENERATE_EMAIL_PATTERN, RandomStringUtils.randomAlphabetic(10, 15))
    }

    static def generateCreateUserRequest() {
        return CreateUserRequest.builder()
                .firstName(RandomStringUtils.randomAlphabetic(6))
                .lastName(RandomStringUtils.randomAlphabetic(6))
                .email(generateEmail())
                .dateOfBirth(LocalDate.now().minusYears(30))
                .password(RandomStringUtils.randomAlphanumeric(6, 10))
                .build()
    }

}
