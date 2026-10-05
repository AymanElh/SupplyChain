package net.ayman.supplychainx.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password cannot be under that 5 characters")
    private String password;
    private String phone;
    @NotNull(message = "Role is required")
    private Long roleId;
}
