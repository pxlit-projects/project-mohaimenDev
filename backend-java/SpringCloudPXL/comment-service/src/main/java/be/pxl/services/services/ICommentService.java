package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;

import java.util.List;

public interface ICommentService {
    
    CommentResponse createComment(CommentRequest request);
    
    List<CommentResponse> getCommentsByPostId(Long postId);
    
    CommentResponse getCommentById(Long id);
    
    CommentResponse updateComment(Long id, CommentRequest request);
    
    void deleteComment(Long id);
}
