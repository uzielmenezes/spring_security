package tech.buildrun.spring_security.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import tech.buildrun.spring_security.controller.dto.CreateTweetRequest;
import tech.buildrun.spring_security.controller.dto.FeedItemResponse;
import tech.buildrun.spring_security.controller.dto.FeedResponse;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.Tweet;
import tech.buildrun.spring_security.repository.TweetRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    @GetMapping("/feed")
    public ResponseEntity<FeedResponse> getFeed(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet -> new FeedItemResponse(tweet.getTweetId(), tweet.getContent(),
                        tweet.getUser().getUsername()));

        return ResponseEntity.ok(new FeedResponse(tweets.getContent(), page, pageSize, tweets.getTotalPages(),
                tweets.getTotalElements()));
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetRequest request,
            JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(request.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

}
