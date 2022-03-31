package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import exception.BlogNotFoundException;
import exception.UserNotFoundException;
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
    public Result showAllBlogs(Integer userId){ return blogService.showAllBlogs(); }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result showBlogByTitle(String title, Integer userId){
        try {
            return blogService.showBlogByTitle(title);
        }catch (BlogNotFoundException e){
            return notFound(e.getMessage());
        }
    }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result showBlogByAuthorName(String authorName, Integer userId){
        return blogService.showBlogByAuthorName(authorName);
    }

    @Pattern("READ_STORAGE")
    @SubjectPresent
    public Result showMyBlogs(Http.Request request){

        try {
            return blogService.showMyBlogs(request);
        }catch (UserNotFoundException e){
            return notFound(e.getMessage());
        }
    }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    @SubjectPresent
    public Result saveBlog(Integer id, Http.Request request){ return blogService.saveBlog(id, request); }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    @SubjectPresent
    public Result updateBlog(Integer id, Http.Request request){ return blogService.updateBlog(id, request); }

    @Pattern({"WRITE_STORAGE", "READ_STORAGE"})
    @SubjectPresent
    public Result deleteBlogByTitle(Integer userId, String blogTitle){
        try {
            return blogService.deleteBlogByTitle(userId, blogTitle);
        }catch (BlogNotFoundException e){
            return notFound(e.getMessage());
        }

    }

}
