package tech.buildrun.spring_security.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.buildrun.spring_security.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
