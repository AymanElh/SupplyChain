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

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SupplyChainXApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainXApplication.class, args);
        List l = new ArrayList();
        l.add(2, "sds");
        l.add(2);
        System.out.println(l);
    }

    @Bean
    CommandLineRunner start(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
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

            if (userRepository.findByEmail("user@gmail.com").isEmpty()) {

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
        };
    }
}
