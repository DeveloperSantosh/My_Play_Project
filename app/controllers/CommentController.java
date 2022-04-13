package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.CommentService;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class CommentController extends Controller {

    public final CommentService commentService;
    public static final int TIMEOUT = 120;

    @Inject
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @SubjectPresent
    public CompletionStage<Result> getComments(String title){
        return CompletableFuture
                .supplyAsync(()->commentService.getCommentsForBlogTitle(title))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @SubjectPresent
    public CompletionStage<Result> saveComment(String title, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->commentService.addCommentsForBlog(title, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }


}
