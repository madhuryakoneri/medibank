package com.audition;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
 class AuditionServiceTest {

    @MockBean
    private transient AuditionIntegrationClient auditionIntegrationClient;

    @Autowired
    private transient AuditionService auditionService;

    @Test
     void testGetPosts() {
        when(auditionIntegrationClient.getPosts()).thenReturn(List.of(getAuditionPost(1, 1, "Test Title", "Test Body"),
                getAuditionPost(2, 2, "Test Title 2", "Test Body 2")));
        final List<AuditionPost> result = auditionService.getPosts();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
     void testGetPostById() {
        when(auditionIntegrationClient.getPostById("1")).thenReturn(getAuditionPost(1, 1, "Test Title", "Test Body"));
        final AuditionPost result = auditionService.getPostById("1");
        assertNotNull(result);
    }

    @Test
     void testGetPostWithComments() {
        when(auditionIntegrationClient.getPostWithComments())
                .thenReturn(List.of(getAuditionPost(1, 1, "Test Title", "Test Body",
                                AuditionPostComments.builder().build()),
                getAuditionPost(2, 2, "Test Title 2", "Test Body 2",
                        AuditionPostComments.builder().build())));
        final List<AuditionPost> result = auditionService.getPostWithComments();
        assertNotNull(result);
    }

    @Test
     void testGetCommentsByPostId() {
        final List<AuditionPostComments> comments = Arrays.asList(AuditionPostComments.builder().build(), AuditionPostComments.builder().build());
        when(auditionIntegrationClient.getCommentsByPostId("1")).thenReturn(comments);
        final List<AuditionPostComments> result = auditionService.getCommentsByPostId("1");
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    private AuditionPost getAuditionPost(final int id, final int userId, final String title, final String body, final AuditionPostComments... comments) {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(id);
        mockPost.setUserId(userId);
        mockPost.setTitle(title);
        mockPost.setBody(body);
        mockPost.setComments(List.of(comments));
        return mockPost;
    }
}
