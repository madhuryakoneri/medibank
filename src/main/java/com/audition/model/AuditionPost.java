package com.audition.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;
    private List<AuditionPostComments> comments = new ArrayList<>();;

    public List<AuditionPostComments> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setComments(final List<AuditionPostComments> comments) {
        this.comments = (comments != null) ? new ArrayList<>(comments) : new ArrayList<>();
    }

}
