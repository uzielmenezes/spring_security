package tech.buildrun.spring_security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.buildrun.spring_security.controller.dto.LoginRequest;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String userEmail;

    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "tb_users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public boolean isLoginCorrect(LoginRequest request, PasswordEncoder encoder) {
        return encoder.matches(request.password(), this.password);
    }
}
