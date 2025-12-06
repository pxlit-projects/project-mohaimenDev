package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByStatus(PostStatus status);
    
    List<Post> findByAuthorContainingIgnoreCase(String author);
    
    List<Post> findByStatusAndAuthorContainingIgnoreCase(PostStatus status, String author);
    
    List<Post> findByStatusAndContentContainingIgnoreCaseOrStatusAndTitleContainingIgnoreCase(
            PostStatus status1, String content, 
            PostStatus status2, String title);
}
