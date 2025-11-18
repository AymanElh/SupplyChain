package net.ayman.supplychainx;

import net.ayman.supplychainx.user.model.Role;
import net.ayman.supplychainx.user.model.User;
import net.ayman.supplychainx.user.repository.RoleRepository;
import net.ayman.supplychainx.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SupplyChainXApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainXApplication.class, args);
    }

    @Bean
    CommandLineRunner start(RoleRepository roleRepository, UserRepository userRepository) {
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
                admin.setPassword("123456");
                admin.setRole(adminRole);

                userRepository.save(admin);
            }
        };
    }
}
