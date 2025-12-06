package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.StatusUpdateRequest;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final IPostService postService;
    
 
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        PostResponse response = postService.updatePost(id, request);
        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<PostResponse> posts;
        
        if (content != null || author != null || startDate != null || endDate != null) {
            posts = postService.filterPosts(content, author, startDate, endDate);
        } else {
            posts = postService.getPublishedPosts();
        }
        
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<PostResponse>> getPendingPosts() {
        List<PostResponse> posts = postService.getPendingPosts();
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PostResponse> updatePostStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        PostResponse response = postService.updatePostStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
