package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import context.MyExecutionContext;
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
    public static final int TIMEOUT = 30;
    public final MyExecutionContext context;
    @Inject
    public CommentController(CommentService commentService, MyExecutionContext context){
        this.commentService = commentService;
        this.context = context;
    }

    @SubjectPresent
    public CompletionStage<Result> getComments(String title){
        return CompletableFuture
                .supplyAsync(()->commentService.getCommentsForBlogTitle(title), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @SubjectPresent
    public CompletionStage<Result> saveComment(String title, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->commentService.addCommentsForBlog(title, request), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }


}
