package tech.buildrun.spring_security.controller.dto;

public record CreateUserRequest(String username, String email, String password) {

}
