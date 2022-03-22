package service;

import dto.RequestComment;
import models.MyComment;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import repository.CommentRepository;
import javax.inject.Inject;
import java.util.List;
import static play.mvc.Results.*;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final FormFactory formFactory;

    @Inject
    public CommentService(FormFactory formFactory) {
        commentRepository = CommentRepository.getInstance();
        this.formFactory = formFactory;
    }

//    Method to get all comments relevant to given blog title from database
    public Result getCommentsForBlogTitle(String title){
        if (title.isBlank()) return badRequest("Blog Title should not be empty");
        List<MyComment> allComments = commentRepository.findAllComments();
        StringBuilder result = new StringBuilder();
        for(MyComment c: allComments){
            if(c.getBlog().getTitle().equals(title)){
                result.append(c).append("\n");
            }
        }
        return ok(result.toString());
    }

//    Method to add comments for blog with given title
    public Result addCommentsForBlog(String blogTitle, Http.Request request){
        if (blogTitle.isBlank())
            return badRequest("Blog Title should not be empty");
        Form<RequestComment> requestCommentForm =  formFactory.form(RequestComment.class).bindFromRequest(request);
        if(requestCommentForm.hasErrors())
            return badRequest("Error in form data.");
        RequestComment requestComment = requestCommentForm.get();
        if (!requestComment.validate().equals("valid"))
            return badRequest(requestComment.validate());
        MyComment newComment = requestComment.getMyComment(blogTitle);
        if(commentRepository.save(newComment))
            return ok("Comment added Successfully");
        return internalServerError("Something went wrong");
    }

}
