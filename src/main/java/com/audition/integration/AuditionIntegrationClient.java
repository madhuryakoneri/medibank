package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.List;

import com.audition.model.AuditionPostComments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {


    @Autowired
    private transient RestTemplate restTemplate;

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        try {
            final ResponseEntity<List<AuditionPost>>  response = restTemplate.exchange(BASE_URL + "posts/",
                    HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<AuditionPost>>() {});
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SystemException("Error fetching posts: " + e.getMessage(), e);
        }
    }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {
            final ResponseEntity<AuditionPost> response = restTemplate.exchange(BASE_URL + "posts/" + id,
                    HttpMethod.GET, null, AuditionPost.class, id);
            return response.getBody();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404, e);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                throw new SystemException("Unknown Error message", e);
            }
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.
    public List<AuditionPost> getPostWithComments() {
        try {
            final List<AuditionPost> auditionPost = getPosts();
            auditionPost.stream().forEach(post -> {
                final ResponseEntity<List<AuditionPostComments>>  response = restTemplate.exchange(BASE_URL + "posts/"
                                + post.getId() + "/comments", HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<AuditionPostComments>>() {});
                post.setComments(response.getBody());
            });
            return auditionPost;
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find Comments for Posts", "Resource Not Found",
                        404, e);
            } else {
                throw new SystemException("Unknown Error message ", e);
            }
        }
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    public List<AuditionPostComments> getCommentsByPostId(final String postId) {
        try {
            final ResponseEntity<List<AuditionPostComments>> response = restTemplate.exchange(BASE_URL + "comments?postId=" + postId, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<AuditionPostComments>>() {});

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find Comments for Post with id " + postId, "Resource Not Found",
                        404, e);
            } else {
                throw new SystemException("Unknown Error message ", e);
            }
        }
    }

}
