package tech.buildrun.spring_security.controller;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.spring_security.controller.dto.CreateUserRequest;
import tech.buildrun.spring_security.entities.User;
import tech.buildrun.spring_security.service.UserService;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/all-users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<User>> listUsers() {
        var users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Void> newUser(@RequestBody CreateUserRequest request) {

        userService.newUser(request);
        return ResponseEntity.ok().build();
    }

}
