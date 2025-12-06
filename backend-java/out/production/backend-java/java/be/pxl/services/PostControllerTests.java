package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void testCreatePost_ReturnsCreated() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Test Post")
                .content("This is test content")
                .author("John Doe")
                .asDraft(false)
                .build();

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("This is test content"))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));
    }

    @Test
    void testCreateDraftPost_SavesAsDraft() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Draft Post")
                .content("Draft content")
                .author("Jane Doe")
                .asDraft(true)
                .build();

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void testUpdatePost_UpdatesContent() throws Exception {
        // First create a post
        Post post = Post.builder()
                .title("Original Title")
                .content("Original content")
                .author("Author")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.DRAFT)
                .build();
        Post savedPost = postRepository.save(post);

        // Update the post
        PostRequest updateRequest = PostRequest.builder()
                .title("Updated Title")
                .content("Updated content")
                .author("Author")
                .asDraft(false)
                .build();

        mockMvc.perform(put("/api/posts/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));
    }

    @Test
    void testGetPublishedPosts_ReturnsOnlyPublished() throws Exception {
        // Create a draft post
        postRepository.save(Post.builder()
                .title("Draft")
                .content("Draft content")
                .author("Author")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.DRAFT)
                .build());

        // Create a published post
        postRepository.save(Post.builder()
                .title("Published Post")
                .content("Published content")
                .author("Author")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED)
                .build());

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Published Post"))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void testFilterPostsByAuthor_ReturnsFilteredPosts() throws Exception {
        postRepository.save(Post.builder()
                .title("Post 1")
                .content("Content 1")
                .author("John Doe")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED)
                .build());

        postRepository.save(Post.builder()
                .title("Post 2")
                .content("Content 2")
                .author("Jane Doe")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED)
                .build());

        mockMvc.perform(get("/api/posts")
                        .param("author", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].author").value("John Doe"));
    }

    @Test
    void testFilterPostsByContent_ReturnsFilteredPosts() throws Exception {
        postRepository.save(Post.builder()
                .title("News Update")
                .content("Breaking news about technology")
                .author("Reporter")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED)
                .build());

        postRepository.save(Post.builder()
                .title("Sports Report")
                .content("Latest sports results")
                .author("Reporter")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.PUBLISHED)
                .build());

        mockMvc.perform(get("/api/posts")
                        .param("content", "technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("News Update"));
    }

    @Test
    void testGetPostById_ReturnsPost() throws Exception {
        Post post = postRepository.save(Post.builder()
                .title("Test Post")
                .content("Test content")
                .author("Author")
                .createdDate(LocalDateTime.now())
                .status(PostStatus.DRAFT)
                .build());

        mockMvc.perform(get("/api/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("Test Post"));
    }
}
