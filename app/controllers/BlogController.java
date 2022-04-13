package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import exception.BlogNotFoundException;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.BlogService;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class BlogController extends Controller {

    public final BlogService blogService;
    public static final int TIMEOUT = 120;

    @Inject
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public CompletionStage<Result> showAllBlogs(){
        return CompletableFuture
                .supplyAsync(blogService::showAllBlogs)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern("READ_STORAGE")
    public CompletionStage<Result> searchBlogByKeyword(String keyword){
        return CompletableFuture
                .supplyAsync(()->blogService.showBlogByKeyword(keyword))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    public CompletionStage<Result> showMyBlogs(Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.showMyBlogs(request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> saveBlog(Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.saveBlog(request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> updateBlog(Http.Request request, String title){
        return CompletableFuture
                .supplyAsync(()->blogService.updateBlog(request, title))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public CompletionStage<Result> deleteBlogByTitle( String blogTitle, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->blogService.deleteBlogByTitle(blogTitle, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }
}
