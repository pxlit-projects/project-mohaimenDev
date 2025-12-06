package be.pxl.services.services;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;

import java.time.LocalDateTime;
import java.util.List;


public interface IPostService {
    
   
    PostResponse createPost(PostRequest request);
    
    PostResponse getPostById(Long id);
  
    PostResponse updatePost(Long id, PostRequest request);
    
    List<PostResponse> getPublishedPosts();
   
    List<PostResponse> filterPosts(String content, String author, LocalDateTime startDate, LocalDateTime endDate);
    
    List<PostResponse> getPendingPosts();
    
    PostResponse updatePostStatus(Long id, String status);
}
