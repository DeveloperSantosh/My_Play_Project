package service;

import models.MyBlog;
import models.RequestBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.UserRepository;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static play.mvc.Results.*;

@Service
public class BlogService {

    private final Logger logger;
    private final BlogRepository blogRepository;
    FormFactory formFactory;

    @Inject
    public BlogService(FormFactory formFactory) {
        logger = LoggerFactory.getLogger(BlogService.class);
        this.blogRepository = BlogRepository.getInstance();
        this.formFactory = formFactory;
    }

//    Method to get blogs from database
    public Result getBlogs(){
        StringBuilder result = new StringBuilder();
        try {
            for(MyBlog blog: blogRepository.findAllBlogs()){
                result.append(blog.toString()).append("\n");
            }
        }catch (SQLException e){
            logger.debug(e.getMessage());
            return internalServerError("Something went wrong");
        }
        return ok(result.toString());
    }

//    Method to retrieve blog from database with given title
    public Result showBlog(String title){
        try {
            MyBlog blog = blogRepository.findBlogByTitle(title);
            return ok(blog.toString());
        }catch (SQLException e){
            logger.debug(e.getMessage());
            return notFound(title+" Blog not found.");
        }
    }

//    Method to save blog to database
    public Result saveBlog(Integer authorId, Http.Request request){
        Form<RequestBlog> requestBlogForm =  formFactory.form(RequestBlog.class).bindFromRequest(request);
        if(requestBlogForm.hasErrors()){ return badRequest("Error in form data.");}
        RequestBlog requestBlog = requestBlogForm.get();

        List<String> imagePaths = saveImagesAndGetPath(request, requestBlog.getTitle());
        if(imagePaths.isEmpty()) return internalServerError("Something went wrong");

        try {
            MyBlog newBlog = MyBlog.newBuilder()
                    .setTitle(requestBlog.getTitle())
                    .setContent(requestBlog.getContent())
                    .setAuthor(UserRepository.getInstance().findUserByID(authorId))
                    .setTimestamp(getCurrentTimeStamp())
                    .addAllImagePath(imagePaths)
                    .build();
            if(blogRepository.save(newBlog))
                return ok("Blog saved Successfully");
            return internalServerError("Could not save blog.");
        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError("Something Went Wrong with SQL.");
        }
    }

    public Result deleteBlog(Integer userId, String blogTitle) {
        MyBlog blog;
        try {
            blog = blogRepository.findBlogByTitle(blogTitle);
            if (blog.getAuthor().getId() == userId){
                blogRepository.delete(blog);
                return ok("respond: Blog deleted successfully.\ndata: "+blog);
            }
            return badRequest("Only author can delete it.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return internalServerError("Something went wrong");
        }
    }

//    Method to save picture in assets folder with package name as Blog Title
    public List<String> saveImagesAndGetPath(Http.Request request, String blogTitle){
        List<String> imagesPath = new ArrayList<>();
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart<Files.TemporaryFile>> fileParts = body.getFiles();

        for (Http.MultipartFormData.FilePart<Files.TemporaryFile> picture : fileParts) {
            String fileName = picture.getFilename();
            String dir = "app/assets/images/'" + blogTitle + "'/";
            Path filepath = Paths.get(dir);
            try {
                java.nio.file.Files.createDirectories(filepath);
                Files.TemporaryFile file = picture.getRef();
                file.copyTo(Paths.get(dir+fileName), true);
                imagesPath.add(String.valueOf(file.path()));
                logger.debug("File Uploaded Successfully.");
            } catch (IOException ex) {
                logger.debug("Could not create directory.");
            }
        }
        return imagesPath;
    }

//    Method to get current timestamp
    public static String getCurrentTimeStamp(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }

}
