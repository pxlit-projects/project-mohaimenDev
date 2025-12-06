package be.pxl.services;

import be.pxl.services.client.PostServiceClient;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.StatusUpdateRequest;
import be.pxl.services.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewControllerTests {

    @Container
    private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("eureka.client.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private PostServiceClient postServiceClient;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
    }

    @Test
    void testGetPendingPosts_ReturnsPendingPosts() throws Exception {
        List<PostResponse> mockPosts = Arrays.asList(
                PostResponse.builder()
                        .id(1L)
                        .title("Pending Post 1")
                        .content("Content 1")
                        .author("Author 1")
                        .status("PENDING_REVIEW")
                        .createdDate(LocalDateTime.now())
                        .build(),
                PostResponse.builder()
                        .id(2L)
                        .title("Pending Post 2")
                        .content("Content 2")
                        .author("Author 2")
                        .status("PENDING_REVIEW")
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        when(postServiceClient.getPendingPosts()).thenReturn(mockPosts);

        mockMvc.perform(get("/api/reviews/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Pending Post 1"))
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"));
    }

    @Test
    void testApprovePost_CreatesReviewAndUpdatesStatus() throws Exception {
        PostResponse mockUpdatedPost = PostResponse.builder()
                .id(1L)
                .title("Test Post")
                .content("Content")
                .author("Author")
                .status("PUBLISHED")
                .createdDate(LocalDateTime.now())
                .build();

        when(postServiceClient.updatePostStatus(eq(1L), any(StatusUpdateRequest.class)))
                .thenReturn(mockUpdatedPost);

        ReviewRequest request = ReviewRequest.builder()
                .author("Reviewer")
                .comment("Great content!")
                .build();

        mockMvc.perform(post("/api/reviews/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.author").value("Reviewer"))
                .andExpect(jsonPath("$.approved").value(true));

        List<Review> reviews = reviewRepository.findAll();
        assert reviews.size() == 1;
        assert reviews.get(0).isApproved();
    }

    @Test
    void testRejectPost_CreatesReviewWithComment() throws Exception {
        PostResponse mockUpdatedPost = PostResponse.builder()
                .id(1L)
                .title("Test Post")
                .content("Content")
                .author("Author")
                .status("REJECTED")
                .createdDate(LocalDateTime.now())
                .build();

        when(postServiceClient.updatePostStatus(eq(1L), any(StatusUpdateRequest.class)))
                .thenReturn(mockUpdatedPost);

        ReviewRequest request = ReviewRequest.builder()
                .author("Reviewer")
                .comment("Please fix the spelling errors")
                .build();

        mockMvc.perform(post("/api/reviews/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.author").value("Reviewer"))
                .andExpect(jsonPath("$.comment").value("Please fix the spelling errors"))
                .andExpect(jsonPath("$.approved").value(false));

        List<Review> reviews = reviewRepository.findAll();
        assert reviews.size() == 1;
        assert !reviews.get(0).isApproved();
        assert reviews.get(0).getComment().equals("Please fix the spelling errors");
    }

    @Test
    void testGetReviewsByPostId_ReturnsReviews() throws Exception {
        Review review1 = Review.builder()
                .postId(1L)
                .author("Reviewer 1")
                .comment("Good")
                .approved(true)
                .reviewDate(LocalDateTime.now())
                .build();
        Review review2 = Review.builder()
                .postId(1L)
                .author("Reviewer 2")
                .comment("Needs work")
                .approved(false)
                .reviewDate(LocalDateTime.now())
                .build();
        reviewRepository.saveAll(Arrays.asList(review1, review2));

        mockMvc.perform(get("/api/reviews/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
