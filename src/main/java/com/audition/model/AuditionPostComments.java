package com.audition.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class AuditionPostComments {
    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;
    public AuditionPostComments() {}
    public AuditionPostComments(final int postId, final int id, final String name, final String email, final String body) {
        this.postId = postId;
        this.id = id;
        this.name = name;
        this.email = email;
        this.body = body;
    }
}
