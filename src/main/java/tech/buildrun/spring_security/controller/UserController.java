package tech.buildrun.spring_security.controller;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.controller.dto.LoginRequest;
import tech.buildrun.spring_security.controller.dto.LoginResponse;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.repository.RoleRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<User>> listUsers() {
        var users = userRepository.findAll();

        return ResponseEntity.ok(users);
    }

}
