package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.MyBlog;
import models.RequestBlog;
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
    List<MyBlog> blogs;

    @Inject
    public BlogController(FormFactory formFactory) {
        this.formFactory = formFactory;
        blogRepository = BlogRepository.getInstance();
        blogs = new ArrayList<>();
    }

    @Restrict(@Group({"USER"}))
    public Result getBlogCount(Integer userId) throws SQLException {
        blogs = blogRepository.findAllBlogs();
        StringBuilder result = new StringBuilder();
        for(MyBlog blog: blogs){
            result.append(blog.toString()).append("\n");
        }
        return ok(result.toString());
    }

    @Restrict(@Group({"USER"}))
    public Result showBlog(String title, Integer userId){
        try {
            return ok(blogRepository.findBlogByTitle(title).toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Restrict(@Group({"USER"}))
//    public Result createBlog(Integer userId){
//        Form<Blog> form = formFactory.form(Blog.class);
//        return ok(views.html.blog.create.render(form, userId));
//    }

    @Restrict(@Group({"USER"}))
    public Result saveBlog(Integer authorId, Http.Request request) throws SQLException {
        Form<RequestBlog> requestBlogForm =  formFactory.form(RequestBlog.class).bindFromRequest(request);
        if(requestBlogForm.hasErrors()){
            return badRequest("Error in form data.");
        }
        RequestBlog requestBlog = requestBlogForm.get();
        MyBlog newBlog = MyBlog.newBuilder()
                .setTitle(requestBlog.getTitle())
                .setContent(requestBlog.getContent())
                .setAuthor(UserRepository.getInstance().findUserByID(authorId))
                .setTimestamp(CommentController.getCurrentTimeStamp())
                .build();
        try {
            if(blogRepository.save(newBlog))
                return ok("Blog saved Successfully");
            return internalServerError("Could not save blog.");
        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError("Could not save blog.");
        }
    }


}
