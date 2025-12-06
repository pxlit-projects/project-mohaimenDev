package be.pxl.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.repository.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CommentControllerTests {

    @Container
    private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysqlContainer.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true");
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("eureka.client.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    void testCreateComment_US10() throws Exception {
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .author("Jan")
                .content("Dit is mijn mening over dit artikel!")
                .build();

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.author").value("Jan"))
                .andExpect(jsonPath("$.content").value("Dit is mijn mening over dit artikel!"));
    }

    @Test
    void testGetCommentsByPostId_US11() throws Exception {
        Comment comment1 = Comment.builder()
                .postId(1L)
                .author("Jan")
                .content("First comment")
                .createdDate(LocalDateTime.now().minusHours(1))
                .build();
        Comment comment2 = Comment.builder()
                .postId(1L)
                .author("Marie")
                .content("Second comment")
                .createdDate(LocalDateTime.now())
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        mockMvc.perform(get("/api/comments/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateComment_US12() throws Exception {
        Comment comment = Comment.builder()
                .postId(1L)
                .author("Jan")
                .content("Original comment")
                .createdDate(LocalDateTime.now())
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest updateRequest = CommentRequest.builder()
                .postId(1L)
                .author("Jan")
                .content("Updated comment content")
                .build();

        mockMvc.perform(put("/api/comments/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment content"));
    }

    @Test
    void testDeleteComment_US12() throws Exception {
        Comment comment = Comment.builder()
                .postId(1L)
                .author("Jan")
                .content("Comment to delete")
                .createdDate(LocalDateTime.now())
                .build();
        Comment savedComment = commentRepository.save(comment);

        mockMvc.perform(delete("/api/comments/" + savedComment.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/comments/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
