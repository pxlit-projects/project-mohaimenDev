package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.StatusUpdateRequest;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    
    private final IPostService postService;
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        log.info("Received request to create post with title: {}", request.getTitle());
        PostResponse response = postService.createPost(request);
        log.info("Post created successfully with id: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        log.info("Fetching post with id: {}", id);
        PostResponse response = postService.getPostById(id);
        log.debug("Post found: {}", response.getTitle());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        log.info("Updating post with id: {}", id);
        PostResponse response = postService.updatePost(id, request);
        log.info("Post updated successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching posts with filters - content: {}, author: {}, startDate: {}, endDate: {}", 
                content, author, startDate, endDate);
        
        List<PostResponse> posts;
        
        if (content != null || author != null || startDate != null || endDate != null) {
            posts = postService.filterPosts(content, author, startDate, endDate);
            log.info("Filtered posts count: {}", posts.size());
        } else {
            posts = postService.getPublishedPosts();
            log.info("Published posts count: {}", posts.size());
        }
        
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts() {
        log.info("Fetching pending posts");
        List<PostResponse> posts = postService.getPendingPosts();
        log.info("Pending posts count: {}", posts.size());
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PostResponse> updatePostStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        log.info("Updating status of post {} to {}", id, request.getStatus());
        PostResponse response = postService.updatePostStatus(id, request.getStatus());
        log.info("Post status updated successfully");
        return ResponseEntity.ok(response);
    }
}
