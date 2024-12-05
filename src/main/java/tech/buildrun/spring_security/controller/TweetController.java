package tech.buildrun.spring_security.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.spring_security.controller.dto.CreateTweetRequest;
import tech.buildrun.spring_security.controller.dto.FeedItemResponse;
import tech.buildrun.spring_security.controller.dto.FeedResponse;
import tech.buildrun.spring_security.service.TweetService;

@RestController
@AllArgsConstructor
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    @GetMapping("/feed")
    public ResponseEntity<FeedResponse> getFeed(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var feed = tweetService.getFeed(page, pageSize);

        return ResponseEntity.ok(feed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedItemResponse> getTweet(@PathVariable("id") Long tweetId) {

        var feedItem = tweetService.getTweet(tweetId);

        return ResponseEntity.ok(feedItem);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetRequest request,
                                            JwtAuthenticationToken token) {

        tweetService.createTweet(request, token);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {

        tweetService.deleteTweet(tweetId, token);

        return ResponseEntity.ok().build();
    }

}
