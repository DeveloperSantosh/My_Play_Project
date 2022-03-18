package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
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

    @Group("USER")
    public Result getComments(String title, Integer userId){
        return commentService.getCommentsForBlogTitle(title);
    }

    @Restrict({@Group("USER"), @Group("ADMIN")})
    public Result saveComment(String title, Integer userId, Http.Request request){
        return commentService.addCommentsForBlog(title, request);
    }


}
