package net.ayman.supplychainx.user.controller;

import lombok.RequiredArgsConstructor;
import net.ayman.supplychainx.common.exception.UnauthorizedException;
import net.ayman.supplychainx.common.security.local.LocalJwtService;
import net.ayman.supplychainx.user.dto.login.LoginRequestDTO;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Profile({"dev", "test"})
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LocalAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalJwtService localJwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return ResponseEntity.ok(Map.of("accessToken", localJwtService.generateToken(user)));
    }
}
