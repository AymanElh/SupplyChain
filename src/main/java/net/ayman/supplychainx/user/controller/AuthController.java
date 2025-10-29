package net.ayman.supplychainx.user.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import net.ayman.supplychainx.user.dto.UserRequestDTO;
import net.ayman.supplychainx.user.dto.UserResponseDTO;
import net.ayman.supplychainx.user.dto.login.LoginRequestDTO;
import net.ayman.supplychainx.user.dto.login.LoginResponseDTO;
import net.ayman.supplychainx.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        LoginResponseDTO loginResp = userService.login(loginRequestDTO);
        session.setAttribute("userId", loginResp.getUserId());
        session.setAttribute("userName", loginResp.getName());
        session.setAttribute("userEmail", loginResp.getEmail());
        session.setAttribute("userRole", loginResp.getRoleName());
        return ResponseEntity.ok(loginResp);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successfully");
    }
}
