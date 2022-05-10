package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.inject.Inject;
import context.MyExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.BlogService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class BlogController extends Controller {

    private final BlogService blogService;
    private final MyExecutionContext context;
    public static final int TIMEOUT = 30;

    @Inject
    public BlogController(BlogService blogService, MyExecutionContext context) {
        this.blogService = blogService;
        this.context = context;
    }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public CompletionStage<Result> showAllBlogs(){
        return CompletableFuture
                .supplyAsync(blogService::showAllBlogs, context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern("READ_STORAGE")
    public CompletionStage<Result> searchBlogByKeyword(String keyword){
        return CompletableFuture
                .supplyAsync(()->blogService.showBlogByKeyword(keyword), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    public CompletionStage<Result> showMyBlogs(Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.showMyBlogs(request), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> saveBlog(Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.saveBlog(request), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> updateBlog(Http.Request request, String title){
        return CompletableFuture
                .supplyAsync(()->blogService.updateBlog(request, title), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> deleteBlogByTitle( String blogTitle, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.deleteBlogByTitle(blogTitle, request), context)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }
}
