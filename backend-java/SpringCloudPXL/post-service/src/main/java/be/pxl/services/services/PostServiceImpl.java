package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements IPostService {
    
    private final PostRepository postRepository;
   
    @Override
    @Transactional
    public PostResponse createPost(PostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .createdDate(LocalDateTime.now())
                .status(request.isAsDraft() ? PostStatus.DRAFT : PostStatus.PENDING_REVIEW)
                .build();
        
        Post savedPost = postRepository.save(post);
        return mapToResponse(savedPost);
    }
    
    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return mapToResponse(post);
    }
    
    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
       
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(request.getAuthor());
        
        if (request.isAsDraft()) {
            post.setStatus(PostStatus.DRAFT);
        } else {
            post.setStatus(PostStatus.PENDING_REVIEW);
        }
        
        Post updatedPost = postRepository.save(post);
        return mapToResponse(updatedPost);
    }
    
    @Override
    public List<PostResponse> getPublishedPosts() {
        return postRepository.findByStatus(PostStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PostResponse> filterPosts(String content, String author, LocalDateTime startDate, LocalDateTime endDate) {
        List<Post> posts;
        
        if (content != null && author != null) {
            
            posts = postRepository.findByStatus(PostStatus.PUBLISHED).stream()
                    .filter(p -> containsIgnoreCase(p.getContent(), content) || containsIgnoreCase(p.getTitle(), content))
                    .filter(p -> containsIgnoreCase(p.getAuthor(), author))
                    .collect(Collectors.toList());
        } else if (content != null) {
         
            posts = postRepository.findByStatusAndContentContainingIgnoreCaseOrStatusAndTitleContainingIgnoreCase(
                    PostStatus.PUBLISHED, content, PostStatus.PUBLISHED, content);
        } else if (author != null) {
          
            posts = postRepository.findByStatusAndAuthorContainingIgnoreCase(PostStatus.PUBLISHED, author);
        } else {
          
            posts = postRepository.findByStatus(PostStatus.PUBLISHED);
        }
        
        if (startDate != null) {
            posts = posts.stream()
                    .filter(p -> !p.getCreatedDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            posts = posts.stream()
                    .filter(p -> !p.getCreatedDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        
        return posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private boolean containsIgnoreCase(String text, String search) {
        if (text == null || search == null) return false;
        return text.toLowerCase().contains(search.toLowerCase());
    }
    
    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .createdDate(post.getCreatedDate())
                .status(post.getStatus())
                .build();
    }
}
