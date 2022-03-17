package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.BlogService;
import javax.inject.Inject;
import java.sql.SQLException;


public class BlogController extends Controller {

    public final BlogService blogService;

    @Inject
    public BlogController(BlogService blogService) { this.blogService = blogService; }

    @Restrict(@Group({"USER"}))
    public Result getBlogs(Integer userId) throws SQLException {return blogService.getBlogs();}

    @Restrict(@Group({"USER"}))
    public Result showBlog(String title, Integer userId){
        return blogService.showBlog(title);
    }

    @Restrict(@Group({"USER"}))
    public Result saveBlog(Integer id, Http.Request request){ return blogService.saveBlog(id, request); }

    @Restrict(@Group({"USER, ADMIN"}))
    public Result deleteBlog(Integer userId, String blogTitle){ return blogService.deleteBlog(userId, blogTitle); }


}
