package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.CommentService;
import javax.inject.Inject;

public class CommentController extends Controller {

    public final CommentService commentService;

    @Inject
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @SubjectPresent
    public Result getComments(String title){
        return commentService.getCommentsForBlogTitle(title);
    }

    @SubjectPresent
    public Result saveComment(String title, Http.Request request){
        return commentService.addCommentsForBlog(title, request);

    }


}
