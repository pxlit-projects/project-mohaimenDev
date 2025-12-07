package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.services.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    
    private final ICommentService commentService;
    
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        log.info("Creating comment for post {} by author {}", request.getPostId(), request.getAuthor());
        CommentResponse response = commentService.createComment(request);
        log.info("Comment created with id: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        log.info("Fetching comments for post: {}", postId);
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        log.info("Found {} comments for post {}", comments.size(), postId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        log.info("Fetching comment with id: {}", id);
        CommentResponse response = commentService.getCommentById(id);
        log.debug("Comment found: {}", response.getContent());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequest request) {
        log.info("Updating comment with id: {}", id);
        CommentResponse response = commentService.updateComment(id, request);
        log.info("Comment {} updated successfully", id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("Deleting comment with id: {}", id);
        commentService.deleteComment(id);
        log.info("Comment {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
