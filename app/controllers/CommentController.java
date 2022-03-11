package controllers;

import model.Comment;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.CommentRepository;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentController extends Controller {

    CommentRepository commentRepository;
    @Inject
    FormFactory formFactory;

    @Inject
    MessagesApi messagesApi;

    public CommentController(){
        commentRepository = CommentRepository.getInstance();
    }

    public Result showComment(Integer blogId, Integer userId) throws SQLException {
        List<Comment> blogComment = new ArrayList<>();
        List<Comment> allComments = commentRepository.findAllComments();
        for(Comment c:allComments){
            if(c.getBlog().getBlogId() == blogId){
                blogComment.add(c);
            }
        }
        return ok(views.html.comment.show.render(blogId, userId, blogComment));
    }

    public Result addComment(Integer blogId, Integer userId, Http.Request request){
        Form<Comment> commentForm = formFactory.form(Comment.class);
        return ok(views.html.comment.add.render(blogId, userId,commentForm, request, messagesApi.preferred(request)));
    }

    public Result saveComment(Integer blogId, Integer userId, Http.Request request) throws SQLException {
        Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest(request);
        Comment newComment = commentForm.get();
        newComment.setBlog(BlogRepository.getInstance().findBlogById(blogId));
        if(!commentRepository.save(newComment))
            return internalServerError();
        return redirect(routes.CommentController.showComment(blogId, userId));
    }

}
