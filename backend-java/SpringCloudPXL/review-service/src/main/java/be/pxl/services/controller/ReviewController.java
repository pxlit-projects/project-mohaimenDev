package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.services.IReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    
    private final IReviewService reviewService;
    
    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts() {
        log.info("Fetching pending posts for review");
        List<PostResponse> posts = reviewService.getPendingPosts();
        log.info("Found {} pending posts", posts.size());
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping("/{postId}/approve")
    public ResponseEntity<ReviewResponse> approvePost(
            @PathVariable Long postId,
            @RequestBody(required = false) ReviewRequest request) {
        log.info("Approving post with id: {}", postId);
        ReviewResponse response = reviewService.approvePost(postId, request);
        log.info("Post {} approved successfully", postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{postId}/reject")
    public ResponseEntity<ReviewResponse> rejectPost(
            @PathVariable Long postId,
            @RequestBody ReviewRequest request) {
        log.info("Rejecting post with id: {}, reason: {}", postId, request.getComment());
        ReviewResponse response = reviewService.rejectPost(postId, request);
        log.info("Post {} rejected successfully", postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPostId(@PathVariable Long postId) {
        log.info("Fetching reviews for post: {}", postId);
        List<ReviewResponse> reviews = reviewService.getReviewsByPostId(postId);
        log.info("Found {} reviews for post {}", reviews.size(), postId);
        return ResponseEntity.ok(reviews);
    }
}
