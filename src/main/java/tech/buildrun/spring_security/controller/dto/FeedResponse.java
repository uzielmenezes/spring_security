package tech.buildrun.spring_security.controller.dto;

import java.util.List;

public record FeedResponse(List<FeedItemResponse> feedItems, int page, int pageSize, int totalPages,
        Long totalElements) {

}
