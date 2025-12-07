package be.pxl.services.services;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;

import java.util.List;

public interface IReviewService {
    
    List<PostResponse> getPendingPosts();
    
    ReviewResponse approvePost(Long postId);
    
    ReviewResponse rejectPost(Long postId, ReviewRequest request);
    
    List<ReviewResponse> getReviewsByPostId(Long postId);
}
