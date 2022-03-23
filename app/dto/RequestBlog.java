package dto;

import models.Entities;
import models.MyBlog;
import repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestBlog {

    private String title;
    private String content;
    private List<String> imagePaths = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImagePaths() { return imagePaths; }

    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public void addImagePaths(String imagePath) { imagePaths.add(imagePath);}

    public void deleteImagePaths(String imagePath) { imagePaths.remove(imagePath);}

    public String validate(){
        if(title==null || title.isBlank())
            return "Enter proper Title";
        else if (title.length()>100)
            return "Title should not be grater than 100";
        else if (content==null || title.isBlank())
             return "Enter content for blog";
        else if (content.length() >=500)
            return "Content should not be grater than 500";
        else return  "valid";
    }

    public MyBlog getMyBlog(int authorId){
        return MyBlog.newBuilder()
                .setTitle(title)
                .setContent(content)
                .setAuthor(UserRepository.getInstance().findUserByID(authorId))
                .setTimestamp(getCurrentTimeStamp())
                .addAllImagePath(imagePaths)
                .build();
    }

//    Method to get current timestamp
    public static String getCurrentTimeStamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }
}
