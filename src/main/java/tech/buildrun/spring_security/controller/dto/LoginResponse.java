package tech.buildrun.spring_security.controller.dto;

public record LoginResponse(String accessToken, String username, Long expiresIn) {

}
