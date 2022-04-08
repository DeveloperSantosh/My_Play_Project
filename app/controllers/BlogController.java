package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import exception.BlogNotFoundException;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.BlogService;
import javax.inject.Inject;

public class BlogController extends Controller {

    public final BlogService blogService;

    @Inject
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result showAllBlogs(){
        return blogService.showAllBlogs();
    }

    @Pattern("READ_STORAGE")
    public Result searchBlogByKeyword(String keyword){
        return blogService.showBlogByKeyword(keyword);
    }

    public Result showMyBlogs(Http.Request request){
        return blogService.showMyBlogs(request);
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public Result saveBlog(Http.Request request){ return blogService.saveBlog(request); }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public Result updateBlog(Http.Request request){ return blogService.updateBlog(request); }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    public Result deleteBlogByTitle( String blogTitle, Http.Request request){
        try {
            return blogService.deleteBlogByTitle(blogTitle, request);
        }catch (BlogNotFoundException e){
            return notFound(e.getMessage());
        }

    }

}
