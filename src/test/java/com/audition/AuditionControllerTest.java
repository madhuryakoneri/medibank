package com.audition;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import com.audition.web.AuditionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class AuditionControllerTest {

    private final static String TITLE = "Test Post";

    @Mock
    private transient AuditionService auditionService;

    @InjectMocks
    private transient AuditionController auditionController;

    private transient MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditionController).build();
    }

    @Test
     void testGetAuditionPosts() throws Exception {
        when(auditionService.getPostsByUserId(anyInt())).thenReturn(List.of(getAuditionPost()));
        final var result = mockMvc.perform(get("/posts?userId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(TITLE))
                .andReturn();

        assertAuditionPost(result.getResponse().getContentAsString());
    }


    @Test
     void testGetPostById() throws Exception {
        when(auditionService.getPostById("1")).thenReturn(getAuditionPost());
        final var result = mockMvc.perform(get("/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(TITLE))
                .andReturn();
        assertAuditionPost(result.getResponse().getContentAsString());
    }

    @Test
     void testGetCommentsWithPost() throws Exception {
        final AuditionPost mockPost = getAuditionPost();
        mockPost.setComments(List.of(AuditionPostComments.builder().postId(1)
                .id(1).email("testemail@test.com").name("testname").body("testbody").build()));

        when(auditionService.getPostWithComments()).thenReturn(List.of(mockPost));

        final var result = mockMvc.perform(get("/posts/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comments.length()")
                        .value(1))
                .andReturn();
        assertAuditionPost(result.getResponse().getContentAsString());
     }

    @Test
     void testGetCommentsByPostId() throws Exception {
        when(auditionService.getCommentsByPostId("1"))
                .thenReturn(List.of(AuditionPostComments.builder().postId(1).id(1).name("Test")
                        .email("testemail@test.com").body("Test body").build()));

        final var result = mockMvc.perform(get("/comments?postid=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("testemail@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test"))
                .andReturn();
        assertAuditionPost(result.getResponse().getContentAsString());
     }

    private AuditionPost getAuditionPost() {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setUserId(1);
        mockPost.setTitle(TITLE);
        return mockPost;
    }

    private void assertAuditionPost(final String responseBody) {
        assertNotNull(responseBody, "Response body should not be null");
    }
}
