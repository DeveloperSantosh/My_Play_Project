package controllers;

import models.Blog;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogController extends Controller {

    List<Blog> blogs;
    BlogSQLController blogSQLController;

    @Inject
    FormFactory formFactory;

    public BlogController() {
        blogSQLController = new BlogSQLController();
        blogs = blogSQLController.retrieveBlogs();
    }

    public Result home(){
        return ok(views.html.blog.home.render(blogs));
    }

    public Result showBlog(String title){
        for(Blog blog: blogs){
            if(blog.getTitle().equals(title)){
                return ok(views.html.blog.show.render(blog));
            }
        }
        return notFound("Sorry! blog not found");
    }

    public Result createBlog(int userId){
        Form<Blog> form = formFactory.form(Blog.class);
        return ok(views.html.blog.create.render(form, userId));
    }

    public Result saveBlog(int userId, Http.Request request){
        Form<Blog> blogForm = formFactory.form(Blog.class).bindFromRequest(request);
        Blog blog = blogForm.get();
        User user = new UserSQLController().retrieveUserById(userId);
        blog.setAuthor(user);
        blogSQLController.insertBlog(blog);
        blogs = blogSQLController.retrieveBlogs();
        return redirect(routes.BlogController.home());
    }

    public Result deleteBlog(String title){
        for(Blog blog : blogs){
            if(blog.getTitle().equals(title)){
                blogSQLController.deleteBlog(blog);
                blogs = blogSQLController.retrieveBlogs();
                return redirect(routes.BlogController.home());
            }
        }
        return notFound("Sorry! blog not found");
    }

}
