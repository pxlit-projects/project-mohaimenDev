package be.pxl.services.client;

import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.StatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "post-service")
public interface PostServiceClient {
    
    @GetMapping("/api/posts/pending")
    List<PostResponse> getPendingPosts();
    
    @PutMapping("/api/posts/{id}/status")
    PostResponse updatePostStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateRequest request);
    
    @GetMapping("/api/posts/{id}")
    PostResponse getPostById(@PathVariable("id") Long id);
}
