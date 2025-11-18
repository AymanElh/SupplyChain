package net.ayman.supplychainx.user.service;

import net.ayman.supplychainx.common.exception.EmailAlreadyExistException;
import net.ayman.supplychainx.common.exception.ResourceNotFoundException;
import net.ayman.supplychainx.user.dto.UserRequestDTO;
import net.ayman.supplychainx.user.dto.UserResponseDTO;
import net.ayman.supplychainx.user.dto.login.LoginRequestDTO;
import net.ayman.supplychainx.user.dto.login.LoginResponseDTO;
import net.ayman.supplychainx.user.mapper.UserMapper;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> getAll() {
        return userMapper.toResponseDTOList(userRepository.findAll());
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with this id " + id + " not found"));
        return userMapper.toDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistException("This email is already exist");
        }
        return userMapper.toDTO(userRepository.save(user));
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if(!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistException("This email is already exist");
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }

        User updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("This user is not exist with id " + id));
        user.softDelete();
        userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User with this email not fount"));

        if(!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUserId(user.getId());
        loginResponse.setName(user.getName());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setPhone(user.getPhone());
        loginResponse.setRoleName(user.getRole().getName());
        return loginResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}
