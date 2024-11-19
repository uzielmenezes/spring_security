package tech.buildrun.spring_security.controller.dto;

public record FeedItemResponse(Long tweetId, String content, String username) {

}
