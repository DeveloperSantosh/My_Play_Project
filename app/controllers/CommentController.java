package controllers;

import models.Comment;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentController extends Controller {

    Map <String, List<Comment>> comments;

    @Inject
    FormFactory formFactory;

    @Inject
    MessagesApi messagesApi;

    public CommentController(){
        comments = new HashMap<>();
    }

    public Result showComment(String blogTitle){
        List<Comment> c = comments.get(blogTitle);
        if(c == null)
            return notFound("No Comments Found");
        return ok(views.html.comment.show.render(blogTitle, c));
    }

    public Result addComment(String blogTitle, Http.Request request){
        Form<Comment> commentForm = formFactory.form(Comment.class);
        return ok(views.html.comment.add.render(blogTitle, commentForm, request, messagesApi.preferred(request)));
    }

    public Result saveComment(String blogTitle, Http.Request request){
        Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest(request);
        Comment newComment = commentForm.get();
        List<Comment> blogComment = comments.get(blogTitle);
        if(blogComment == null )    blogComment = new ArrayList<>();
        blogComment.add(newComment);
        comments.put(blogTitle, blogComment);
        return redirect(routes.CommentController.showComment(blogTitle));
    }

}
