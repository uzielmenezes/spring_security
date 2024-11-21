package tech.buildrun.spring_security.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.repository.RoleRepository;
import tech.buildrun.spring_security.repository.UserRepository;

@Configuration
@AllArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.err.println("User already exists");
                }, () -> {
                    var user = new User();
                    user.setUsername("admin");
                    user.setUserEmail("admin@gmail.com");
                    user.setPassword(passwordEncoder.encode("123456789"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                });
    }
}
