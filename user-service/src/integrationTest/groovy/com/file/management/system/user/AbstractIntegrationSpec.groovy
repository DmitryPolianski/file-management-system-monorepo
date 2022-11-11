package com.file.management.system.user

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@DirtiesContext
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("integration-test")
@ContextConfiguration(classes = [UserServiceApplication])
class AbstractIntegrationSpec extends Specification {
}
