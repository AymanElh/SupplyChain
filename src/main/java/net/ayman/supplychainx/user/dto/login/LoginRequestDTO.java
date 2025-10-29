package net.ayman.supplychainx.user.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Email filed is required")
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Password field is required")
    private String password;
}
