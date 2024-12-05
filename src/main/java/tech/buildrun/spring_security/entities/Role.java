package tech.buildrun.spring_security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;

    public enum Values {

        ADMIN(1L),

        BASIC(2L);

        Long roleId;

        Values(Long roleId) {
            this.roleId = roleId;
        }

        public Long getRoleId() {
            return roleId;
        }
    }

}
