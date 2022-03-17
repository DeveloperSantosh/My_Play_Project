package service;

import models.MyComment;
import models.RequestComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.CommentRepository;
import javax.inject.Inject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static play.mvc.Results.*;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final FormFactory formFactory;
    public Logger logger;

    @Inject
    public CommentService(FormFactory formFactory) {
        commentRepository = CommentRepository.getInstance();
        this.formFactory = formFactory;
        logger = LoggerFactory.getLogger(CommentService.class);
    }

//    Method to get all comments relevant to given blog title from database
    public Result getCommentsForBlogTitle(String title){
        try {
            List<MyComment> allComments = commentRepository.findAllComments();
            StringBuilder result = new StringBuilder();
            for(MyComment c: allComments){
                if(c.getBlog().getTitle().equals(title)){
                    result.append(c).append("\n");
                }
            }
            return ok(result.toString());
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        return internalServerError("Something went wrong");
    }

//    Method to add comments for blog with given title
    public Result addCommentsForBlog(String blogTitle, Http.Request request){
        Form<RequestComment> requestCommentForm =  formFactory.form(RequestComment.class).bindFromRequest(request);
        if(requestCommentForm.hasErrors()){ return badRequest("Error in form data."); }
        RequestComment requestComment = requestCommentForm.get();

        try {
            MyComment newComment = MyComment.newBuilder()
                    .setComment(requestComment.getComment())
                    .setBlog(BlogRepository.getInstance().findBlogByTitle(blogTitle))
                    .setTimestamp(getCurrentTimeStamp())
                    .build();
            if(commentRepository.save(newComment))
                return ok("Comment added Successfully");
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        return internalServerError("Something went wrong");
    }

//    Method to get current timestamp
    public static String getCurrentTimeStamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }
}
