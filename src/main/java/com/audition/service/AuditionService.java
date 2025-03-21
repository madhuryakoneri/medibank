package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.List;
import java.util.stream.Collectors;

import com.audition.model.AuditionPostComments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    @Autowired
    private transient AuditionIntegrationClient auditionIntegrationClient;

    public List<AuditionPost> getPostsByUserId(final int userId) {
        return auditionIntegrationClient.getPosts()
                    .stream()
                    .filter(post -> post.getUserId() == userId)
                    .collect(Collectors.toList());
    }

    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public List<AuditionPost> getPostWithComments() {
        return auditionIntegrationClient.getPostWithComments();
    }

    public List<AuditionPostComments> getCommentsByPostId(final String postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }
}
