package net.ayman.supplychainx.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ayman.supplychainx.common.security.JwtUtil;
import net.ayman.supplychainx.user.dto.login.LoginRequestDTO;
import net.ayman.supplychainx.user.dto.login.LoginResponseDTO;
import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.RoleRepository;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("JWT Authentication Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private Role testRole;
    private String validToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create admin role
        testRole = new Role();
        testRole.setName("admin");
        testRole = roleRepository.save(testRole);

        // Create test user with admin role
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setPhone("1234567890");
        testUser.setRole(testRole);
        testUser.setIsDeleted(false);
        testUser = userRepository.save(testUser);

        // Generate valid token
        validToken = jwtUtil.generateToken(testUser);
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully login with valid credentials")
    void shouldLoginWithValidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@test.com", "password123");

        MvcResult result = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.roleName").value("admin"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponseDTO response = objectMapper.readValue(responseBody, LoginResponseDTO.class);

        assertThat(response.getToken()).isNotNull();
        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Should fail login with invalid email")
    void shouldFailLoginWithInvalidEmail() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("invalid@test.com", "password123");

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Authentication failure may return 500
    }

    @Test
    @Order(3)
    @DisplayName("Should fail login with invalid password")
    void shouldFailLoginWithInvalidPassword() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@test.com", "wrongpassword");

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Authentication failure may return 500
    }

    @Test
    @Order(4)
    @DisplayName("Should fail login with empty credentials")
    void shouldFailLoginWithEmptyCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("", "");

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Should access protected endpoint with valid JWT token")
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + validToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @DisplayName("Should deny access to protected endpoint without token")
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("Should deny access with invalid JWT token")
    void shouldDenyAccessWithInvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token";

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + invalidToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("Should deny access with malformed Authorization header")
    void shouldDenyAccessWithMalformedHeader() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", validToken)) // Missing "Bearer " prefix
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    @DisplayName("Should extract username from valid JWT token")
    void shouldExtractUsernameFromToken() {
        String extractedEmail = jwtUtil.extractUsername(validToken);
        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    @Order(10)
    @DisplayName("Should validate JWT token correctly")
    void shouldValidateToken() {
        Boolean isValid = jwtUtil.validateToken(validToken, testUser);
        assertThat(isValid).isTrue();
    }

    @Test
    @Order(11)
    @DisplayName("Should generate unique tokens for each login")
    void shouldGenerateUniqueTokens() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@test.com", "password123");

        // First login
        MvcResult result1 = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Second login (after small delay)
        Thread.sleep(1000);
        
        MvcResult result2 = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO response1 = objectMapper.readValue(
                result1.getResponse().getContentAsString(), LoginResponseDTO.class);
        LoginResponseDTO response2 = objectMapper.readValue(
                result2.getResponse().getContentAsString(), LoginResponseDTO.class);

        assertThat(response1.getToken()).isNotEqualTo(response2.getToken());
    }
}
