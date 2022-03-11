package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment{
    private int commentId;
    public String comment;
    public String timestamp;
    private Blog blog;

    public Comment() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        timestamp = dateTime.format(myFormatObj);
    }

    public Comment(String username, String comment) {
        this.comment = comment;
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        timestamp = dateTime.format(myFormatObj);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
