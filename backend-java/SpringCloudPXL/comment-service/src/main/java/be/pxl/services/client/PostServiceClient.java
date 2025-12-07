package be.pxl.services.client;

import be.pxl.services.domain.dto.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service")
public interface PostServiceClient {
    
    @GetMapping("/api/posts/{id}")
    PostResponse getPostById(@PathVariable Long id);
}
