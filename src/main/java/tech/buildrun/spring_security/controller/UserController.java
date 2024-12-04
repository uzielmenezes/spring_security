package tech.buildrun.spring_security.controller;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.repository.RoleRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.util.List;
import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/all-users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<User>> listUsers() {
        var users = userRepository.findAll();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Void> newUser(@RequestBody CreateUserRequest request) {

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var user = userRepository.findByUserEmail(request.email());

        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var newUser = new User();
        newUser.setUsername(request.username());
        newUser.setUserEmail(request.email());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.password()));
        newUser.setRoles(Set.of(basicRole));

        userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

}
