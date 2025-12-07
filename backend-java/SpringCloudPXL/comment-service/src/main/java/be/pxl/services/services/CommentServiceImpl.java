package be.pxl.services.services;

import be.pxl.services.client.PostServiceClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements ICommentService {
    
    private final CommentRepository commentRepository;
    private final PostServiceClient postServiceClient;
    
    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
    
        log.info("Validating post exists with id: {}", request.getPostId());
        try {
            postServiceClient.getPostById(request.getPostId());
            log.info("Post {} exists, creating comment", request.getPostId());
        } catch (FeignException.NotFound e) {
            log.error("Post {} not found", request.getPostId());
            throw new RuntimeException("Post not found with id: " + request.getPostId());
        }
        
        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .author(request.getAuthor())
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with id: {}", savedComment.getId());
        return mapToResponse(savedComment);
    }
    
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        log.info("Fetching comments for post: {}", postId);
        return commentRepository.findByPostIdOrderByCreatedDateDesc(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CommentResponse getCommentById(Long id) {
        log.info("Fetching comment with id: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        return mapToResponse(comment);
    }
    
    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request) {
        log.info("Updating comment with id: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        
        comment.setContent(request.getContent());
        comment.setUpdatedDate(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} updated successfully", id);
        return mapToResponse(updatedComment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
        log.info("Comment {} deleted successfully", id);
    }
    
    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .updatedDate(comment.getUpdatedDate())
                .build();
    }
}
