package com.file.management.system.user.domain.view;

import java.time.LocalDate;

public record UserView(Long id, String firstName, String lastName, LocalDate dateOfBirth, String email) {
}
