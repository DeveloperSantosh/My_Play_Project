package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Blog;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.UserRepository;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlogController extends Controller {


    private final FormFactory formFactory;
    private final BlogRepository blogRepository;
    List<Blog> blogs;

    @Inject
    public BlogController(FormFactory formFactory) {
        this.formFactory = formFactory;
        blogRepository = BlogRepository.getInstance();
        blogs = new ArrayList<>();
    }

    public Result home(Integer userId) throws SQLException {
        blogs = blogRepository.findAllBlogs();
        return ok(views.html.blog.home.render(blogs, userId));
    }

    @Restrict(@Group({"ADMIN"}))
    public Result showBlog(String title, Integer userId){
        try {
            Blog blog = blogRepository.findBlogByTitle(title);
            return ok(views.html.blog.show.render(blog, userId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notFound("Sorry! blog not found");
    }

    @Restrict(@Group({"USER"}))
    public Result createBlog(Integer userId){
        Form<Blog> form = formFactory.form(Blog.class);
        return ok(views.html.blog.create.render(form, userId));
    }

    @Restrict(@Group({"USER"}))
    public Result saveBlog(Integer userId, Http.Request request) {
        Form<Blog> blogForm = formFactory.form(Blog.class).bindFromRequest(request);
        Blog blog = blogForm.get();
        try {
            User user = UserRepository.getInstance().findUserByID(userId);
            blog.setAuthor(user);
            blogRepository.save(blog);
            blogs = blogRepository.findAllBlogs();
            return redirect(routes.BlogController.home(userId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notFound();
    }


}
