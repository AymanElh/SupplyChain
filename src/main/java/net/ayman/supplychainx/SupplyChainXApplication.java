package net.ayman.supplychainx;

import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.RoleRepository;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class SupplyChainXApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainXApplication.class, args);
    }

    @Bean
    CommandLineRunner start(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByEmail("admin@gmail.com").ifPresentOrElse(
                admin -> {
                    admin.setPassword(passwordEncoder.encode("123456"));
                    userRepository.save(admin);
                },
                () -> {
                    Role adminRole = roleRepository.findByName("admin").orElseGet(() -> {
                        Role role = new Role();
                        role.setName("admin");
                        return roleRepository.save(role);
                    });

                    User admin = new User();
                    admin.setName("admin user");
                    admin.setEmail("admin@gmail.com");
                    admin.setPassword(passwordEncoder.encode("123456"));
                    admin.setRole(adminRole);

                    userRepository.save(admin);
                }
            );

            userRepository.findByEmail("user@gmail.com").ifPresentOrElse(
                user -> {
                    user.setPassword(passwordEncoder.encode("123456"));
                    userRepository.save(user);
                },
                () -> {
                    Role userRole = roleRepository.findByName("guest").orElseGet(() -> {
                        Role role = new Role();
                        role.setName("guest");
                        return roleRepository.save(role);
                    });

                    User user = new User();
                    user.setName("user user");
                    user.setEmail("user@gmail.com");
                    user.setPassword(passwordEncoder.encode("123456"));
                    user.setRole(userRole);

                    userRepository.save(user);
                }
            );
        };
    }
}
