package tech.buildrun.spring_security.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.buildrun.spring_security.controller.dto.LoginRequest;
import tech.buildrun.spring_security.controller.dto.LoginResponse;
import tech.buildrun.spring_security.service.AuthService;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        var login = authService.login(request);

        return ResponseEntity.ok(login);
    }
}
