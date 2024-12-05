package tech.buildrun.spring_security.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tech.buildrun.spring_security.controller.dto.LoginRequest;
import tech.buildrun.spring_security.controller.dto.LoginResponse;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.repository.UserRepository;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtEncoder encoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {

        var user = userRepository.findByUserEmail(loginRequest.email());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("User email or password invalid");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.get().getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

        var jwtValue = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, user.get().getUsername(), expiresIn);
    }
}
