package com.audition;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

 class AuditionIntegrationClientTest {

     private static final String TITLE = "Sample Post";

     private static final String BODY = "Sample Post Body";

     private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

     private static final String POST = "posts/";

    @Mock
    private transient RestTemplate restTemplate;

    @InjectMocks
    private transient AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetPostByIdSuccess() throws Exception {

        final String postId = "1";

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), anyString()))
                .thenReturn(ResponseEntity.ok(getAuditionPost(1, 1, TITLE, BODY)));

        final AuditionPost result = auditionIntegrationClient.getPostById(postId);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getUserId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(BODY, result.getBody());
    }

    @Test
    void testGetAllPostsSuccess() throws Exception {

        when(restTemplate.exchange(eq(BASE_URL + POST),
                eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<AuditionPost>>() {})))
                .thenReturn(ResponseEntity.ok(List.of(getAuditionPost(1, 1, TITLE, BODY),
                        getAuditionPost(2, 2, "Sample Post 2", "Sample post Body 2"))));

        final List<AuditionPost> result = auditionIntegrationClient.getPosts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(TITLE, result.get(0).getTitle());
        assertEquals(BODY, result.get(0).getBody());
    }

    @Test
    void testGetPostWithCommentsSuccess() {
        final List<AuditionPost> mockPosts = Arrays.asList(getAuditionPost(1, 1, "Test Post", "Test Body"));
        when(restTemplate.exchange(eq(BASE_URL + POST),
                eq(HttpMethod.GET), any(),
                eq(new ParameterizedTypeReference<List<AuditionPost>>() {})))
                .thenReturn(ResponseEntity.ok(mockPosts));

        final List<AuditionPostComments> mockComments1 = Arrays.asList(AuditionPostComments.builder().id(1).name("Test Comment 1").postId(1).build());

        when(restTemplate.exchange(eq(BASE_URL + POST + "1/comments"),
                eq(HttpMethod.GET), any(),
                eq(new ParameterizedTypeReference<List<AuditionPostComments>>() {})))
                .thenReturn(ResponseEntity.ok(mockComments1));

        final List<AuditionPost> postsWithComments = auditionIntegrationClient.getPostWithComments();

       assertNotNull(postsWithComments);
       assertEquals(1, postsWithComments.size());
       assertNotNull(postsWithComments.get(0).getComments());
       assertEquals(1, postsWithComments.get(0).getComments().size());
    }

    @Test
    void testGetPostById2Success() throws Exception {
        final String postId = "1";

        when(restTemplate.exchange(eq(BASE_URL + "comments?postId=1"), eq(HttpMethod.GET), any(),
                eq(new ParameterizedTypeReference<List<AuditionPostComments>>() {})))
                .thenReturn(ResponseEntity.ok(List.of(AuditionPostComments.builder().postId(1).id(1).name("Test Comment 1").build(),
                        AuditionPostComments.builder().postId(1).id(2).name("Test Comment 2").build())));

        final List<AuditionPostComments> result = auditionIntegrationClient.getCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getPostId());
        assertEquals("Test Comment 1", result.get(0).getName());
        assertEquals(2, result.get(1).getId());
        assertEquals(1, result.get(1).getPostId());
        assertEquals("Test Comment 2", result.get(1).getName());
    }

    @Test
    void testGetPostsThrowSystemException() {
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        when(restTemplate.exchange(eq(BASE_URL + POST), eq(HttpMethod.GET),
                Mockito.isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(exception);

        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());
        assertEquals("Error fetching posts: 400 Bad Request", thrown.getMessage());
    }

    @Test
    void testGetPostByIdThrowSystemException() {
        final String postId = "1";
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        when(restTemplate.exchange(eq(BASE_URL + POST + postId), eq(HttpMethod.GET),
                Mockito.isNull(), eq(AuditionPost.class), eq(postId)))
                .thenThrow(exception);
        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(postId));
        assertEquals("Cannot find a Post with id 1", thrown.getMessage());
    }

    @Test
    void testGetPostByIdThrowSystemExceptionUnknowException() {
        final String postId = "1";
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        when(restTemplate.exchange(eq(BASE_URL + POST + postId), eq(HttpMethod.GET),
                Mockito.isNull(), eq(AuditionPost.class), eq(postId)))
                .thenThrow(exception);

        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostById(postId));
        assertEquals("Unknown Error message", thrown.getMessage());
    }

    @Test
    void testGetPostWithCommentsThrowSystemException() {
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(exception);

        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPostWithComments());
        assertEquals("Error fetching posts: 400 Bad Request", thrown.getMessage());
    }

    @Test
    void testGetCommentsByPostIdThrowSystemException() {
        final String postId = "1";
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        when(restTemplate.exchange(eq(BASE_URL + "comments?postId=" + postId), eq(HttpMethod.GET),
                Mockito.isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(exception);

        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId(postId));
        assertEquals("Unknown Error message ", thrown.getMessage());
    }

    @Test
    void testGetCommentsByPostIdThrowSystemExceptionUnknownErrorOccurs() {
        final String postId = "1";
        final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        when(restTemplate.exchange(eq(BASE_URL + "comments?postId=" + postId), eq(HttpMethod.GET),
                Mockito.isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(exception);

        final SystemException thrown = assertThrows(SystemException.class, () -> auditionIntegrationClient.getCommentsByPostId(postId));
        assertEquals("Unknown Error message ", thrown.getMessage());
    }

    private AuditionPost getAuditionPost(final int id, final int userId, final String title, final String body, final AuditionPostComments... comments) {
        final AuditionPost post = new AuditionPost();
        post.setId(id);
        post.setUserId(userId);
        post.setTitle(title);
        post.setBody(body);
        post.setComments(List.of(comments));
        return post;
    }

}
