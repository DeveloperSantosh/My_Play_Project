package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.MyComment;
import models.RequestComment;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.CommentRepository;
import javax.inject.Inject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Restrict(@Group({"USER"}))
    public Result getCommentCount(String title, Integer userId) throws SQLException {
        List<MyComment> blogComment = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for(MyComment c:commentRepository.findAllComments()){
            if(c.getBlog().getTitle().equals(title)){
                blogComment.add(c);
                result.append(c.toString()).append("\n");
            }
        }
//        return ok("Total Comments Found: "+blogComment.size());
        return ok(result.toString());
    }

//    @Restrict(@Group({"USER"}))
//    public Result addComment(Integer blogId, Integer userId, Http.Request request){
//        Form<Comment> commentForm = formFactory.form(Comment.class);
//        return ok(views.html.comment.add.render(blogId, userId,commentForm, request, messagesApi.preferred(request)));
//    }

    @Restrict(@Group({"USER"}))
    public Result saveComment(String title, Integer userId, Http.Request request) throws SQLException {
        String comment = request.body().asJson().get("comment").textValue();
        MyComment newComment = MyComment.newBuilder()
                .setComment(comment)
                .setBlog(BlogRepository.getInstance().findBlogByTitle(title))
                .setTimestamp(getCurrentTimeStamp())
                .build();
        if(!commentRepository.save(newComment))
            return internalServerError("Could not add comment.");
        return ok("Comment added Successfully");
    }

    public Result viewComment(String title, Integer userId, Integer commentId) throws SQLException {
        for(MyComment c: commentRepository.findAllComments()){
            if(c.getBlog().getTitle().equals(title)){
                if(c.getId()==userId)
                    return ok(c.toString());
            }
            return notFound("Blog not found with title: "+title);
        }
        return notFound("No Comment Found with id:"+ commentId);
    }

    public static String getCurrentTimeStamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }

}
