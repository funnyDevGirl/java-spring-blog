package io.hexlet.blog.controller.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hexlet.blog.dto.PostDTO;
import io.hexlet.blog.model.Post;
import io.hexlet.blog.repository.PostRepository;
import io.hexlet.blog.util.ModelFaker;
import io.hexlet.blog.util.UserUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class PostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ModelFaker modelFaker;

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/posts").with(jwt()))
                .andExpect(status().isOk());
    }

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    public void testCreate() throws Exception {
        var data = new PostDTO();
        data.setSlug("someslug");
        data.setName("somename");
        data.setBody("somebody");

        var request = post("/api/posts")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var post = postRepository.findBySlug(data.getSlug());
        assertNotNull(post.get());
    }

    @Test
    public void testShow() throws Exception {
        var post = modelFaker.fake(Post.class);
        post.setAuthor(userUtils.getTestUser());
        postRepository.save(post);

        var request = get("/api/posts/" + post.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}