package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    
    private final CommentRepository commentRepository;
    
    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .author(request.getAuthor())
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }
    
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedDateDesc(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        return mapToResponse(comment);
    }
    
    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        
        comment.setContent(request.getContent());
        comment.setUpdatedDate(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        return mapToResponse(updatedComment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
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
