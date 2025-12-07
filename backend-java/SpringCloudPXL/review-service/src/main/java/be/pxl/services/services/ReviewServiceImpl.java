package be.pxl.services.services;

import be.pxl.services.client.PostServiceClient;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements IReviewService {
    
    private final ReviewRepository reviewRepository;
    private final PostServiceClient postServiceClient;
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public List<PostResponse> getPendingPosts() {
        return postServiceClient.getPendingPosts();
    }
    
    @Override
    @Transactional
    public ReviewResponse approvePost(Long postId, ReviewRequest request) {
        // Use request values if provided, otherwise use defaults
        String author = (request != null && request.getAuthor() != null) ? request.getAuthor() : "System";
        String comment = (request != null) ? request.getComment() : null;
        
        Review review = Review.builder()
                .postId(postId)
                .author(author)
                .comment(comment)
                .approved(true)
                .reviewDate(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        postServiceClient.updatePostStatus(postId, 
                StatusUpdateRequest.builder().status("PUBLISHED").build());

        String message = "Post " + postId + " has been APPROVED and published!";
        rabbitTemplate.convertAndSend("notificationQueue", message);
        log.info("Notification sent: {}", message);
        
        return mapToResponse(savedReview);
    }
    
    @Override
    @Transactional
    public ReviewResponse rejectPost(Long postId, ReviewRequest request) {
        Review review = Review.builder()
                .postId(postId)
                .author(request.getAuthor())
                .comment(request.getComment())
                .approved(false)
                .reviewDate(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        postServiceClient.updatePostStatus(postId, 
                StatusUpdateRequest.builder().status("REJECTED").build());

        String message = "Post " + postId + " has been REJECTED. Reason: " + request.getComment();
        rabbitTemplate.convertAndSend("notificationQueue", message);
        log.info("Notification sent: {}", message);
        
        return mapToResponse(savedReview);
    }
    
    @Override
    public List<ReviewResponse> getReviewsByPostId(Long postId) {
        return reviewRepository.findByPostId(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .postId(review.getPostId())
                .author(review.getAuthor())
                .comment(review.getComment())
                .approved(review.isApproved())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
