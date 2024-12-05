package tech.buildrun.spring_security.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.spring_security.controller.dto.CreateTweetRequest;
import tech.buildrun.spring_security.controller.dto.FeedItemResponse;
import tech.buildrun.spring_security.controller.dto.FeedResponse;
import tech.buildrun.spring_security.entities.Role;
import tech.buildrun.spring_security.entities.Tweet;
import tech.buildrun.spring_security.repository.TweetRepository;
import tech.buildrun.spring_security.repository.UserRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public FeedResponse getFeed(int page, int pageSize) {

        var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet -> new FeedItemResponse(tweet.getTweetId(), tweet.getContent(),
                        tweet.getUser().getUsername()));

        return new FeedResponse(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements());
    }

    public FeedItemResponse getTweet(Long tweetId) {
        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet not found"));

        return new FeedItemResponse(tweetId, tweet.getContent(), tweet.getUser().getUsername());
    }

    public void createTweet(CreateTweetRequest createTweetRequest, JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Tweet tweet = Tweet.builder()
                .user(user)
                .content(createTweetRequest.content())
                .build();

        tweetRepository.save(tweet);
    }

    public void deleteTweet(Long tweetId, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet not found"));

        var isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this tweet");
        }
    }
}
