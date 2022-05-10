package dto;

import models.MyComment;
import repository.BlogRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestComment {

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String validate(){
        if(comment.isBlank())
            return "Comment should not be blank.";
        else if (comment.length()>100)
            return "comment length should be <100";
        else return "valid";
    }

    public MyComment getMyComment(String blogTitle){
        return MyComment.newBuilder()
                .setComment(comment)
                .setBlog(BlogRepository.getInstance().findBlogByTitle(blogTitle))
                .setTimestamp(getCurrentTimeStamp())
                .build();
    }

    //    Method to get current timestamp
    public static String getCurrentTimeStamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }
}
