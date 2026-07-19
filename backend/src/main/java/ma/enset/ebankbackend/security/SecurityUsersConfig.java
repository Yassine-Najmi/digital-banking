package ma.enset.ebankbackend.security;

import ma.enset.ebankbackend.entities.AppUser;
import ma.enset.ebankbackend.repositories.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class SecurityUsersConfig {

    @Bean
    @Order(1)
    CommandLineRunner seedSecurityUsers(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.findByUsername("user1").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .id(UUID.randomUUID().toString())
                        .username("user1")
                        .password(passwordEncoder.encode("12345"))
                        .roles("USER")
                        .build());
            }
            if (appUserRepository.findByUsername("admin").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .id(UUID.randomUUID().toString())
                        .username("admin")
                        .password(passwordEncoder.encode("12345"))
                        .roles("USER,ADMIN")
                        .build());
            }
        };
    }
}
