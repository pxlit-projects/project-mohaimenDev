package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final IReviewService reviewService;
    
    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts() {
        List<PostResponse> posts = reviewService.getPendingPosts();
        return ResponseEntity.ok(posts);
    }
    
    @PostMapping("/{postId}/approve")
    public ResponseEntity<ReviewResponse> approvePost(@PathVariable Long postId) {
        ReviewResponse response = reviewService.approvePost(postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{postId}/reject")
    public ResponseEntity<ReviewResponse> rejectPost(
            @PathVariable Long postId,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.rejectPost(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPostId(@PathVariable Long postId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByPostId(postId);
        return ResponseEntity.ok(reviews);
    }
}
