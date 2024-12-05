package tech.buildrun.spring_security.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.repository.RoleRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public void newUser(CreateUserRequest userRequest) {

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        userRepository.findByUserEmail(userRequest.email())
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
                });

        User newUser = User.builder()
                .username(userRequest.username())
                .userEmail(userRequest.email())
                .password(bCryptPasswordEncoder.encode(userRequest.password()))
                .roles(Set.of(basicRole))
                .build();

        userRepository.save(newUser);

    }
}
