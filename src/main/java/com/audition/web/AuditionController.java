package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComments;
import com.audition.service.AuditionService;
import java.util.List;

import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@Validated
public class AuditionController {

    @Autowired
    private transient AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam final Integer userId) {

        // TODO Add logic that filters response data based on the query param
        return  auditionService.getPostsByUserId(userId);
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPosts(@Pattern(regexp = "^[0-9]+$", message = "Invalid ID. ID should be an integer")
                                                   @PathVariable("id")   final String postId) {
        // TODO Add input validation
        return auditionService.getPostById(postId);
    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/
    @RequestMapping(value = "/posts/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getCommentsWithPost() {
        return auditionService.getPostWithComments();
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPostComments> getCommentsByPostId(@Pattern(regexp = "^[0-9]+$", message = "Invalid ID. ID should be an integer")
                                                                            @RequestParam("postid") final String postId) {
        return auditionService.getCommentsByPostId(postId);
    }

}
