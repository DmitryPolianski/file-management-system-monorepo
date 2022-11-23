package com.file.management.system.user.domain.request;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserRequest {

    @Size(max = 32)
    @NotEmpty(message = "First name should not be empty")
    private String firstName;

    @Size(max = 32)
    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @NotNull(message = "Date of birth should not be empty")
    private LocalDate dateOfBirth;

    @Email
    @Size(min = 6, max = 128)
    @NotEmpty(message = "Email should not be empty")
    private String email;

    @ToString.Exclude
    @Size(min = 6, max = 10)
    @NotEmpty(message = "Password should not be empty")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])[0-9a-zA-Z]{6,10}",
      message = "Invalid password. The password must contain numbers and letters, the length of the password must be between 6 and 10.")
    private String password;

}
