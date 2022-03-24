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
    public BlogController(BlogService blogService) { this.blogService = blogService; }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result getBlogs(Integer userId){ return blogService.getBlogs(); }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result showBlog(String title, Integer userId){
        try {
            return blogService.showBlog(title);
        }catch (BlogNotFoundException e){
            return notFound(e.getMessage());
        }
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    @SubjectPresent
    public Result saveBlog(Integer id, Http.Request request){ return blogService.saveBlog(id, request); }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    @SubjectPresent
    public Result deleteBlog(Integer userId, String blogTitle){
        try {
            return blogService.deleteBlog(userId, blogTitle);
        }catch (BlogNotFoundException e){
            return notFound(e.getMessage());
        }

    }

}
