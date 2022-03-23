package service;

import models.MyBlog;
import dto.RequestBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.CSRF;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static play.mvc.Results.*;

@Service
public class BlogService {

    private final Logger logger = LoggerFactory.getLogger(BlogService.class);
    private final BlogRepository blogRepository;
    FormFactory formFactory;

    @Inject
    public BlogService(FormFactory formFactory) {
        this.blogRepository = BlogRepository.getInstance();
        this.formFactory = formFactory;
    }

//    Method to get blogs from database
    public Result getBlogs(){

        StringBuilder result = new StringBuilder();
        for(MyBlog blog: blogRepository.findAllBlogs()){
            result.append(blog.toString()).append("\n");
        }
        return ok(result.toString());
    }

//    Method to retrieve blog from database with given title
    public Result showBlog( String title){
        if (title.trim().isEmpty()) return badRequest("Blog title should not be Empty");
        MyBlog blog = blogRepository.findBlogByTitle(title);
        if (blog == null) return notFound("BLOG NOT FOUND WITH TITLE: "+title);
        return ok(blog.toString());
    }

//    Method to save blog to database
    public Result saveBlog(Integer authorId, Http.Request request){
        Form<RequestBlog> requestBlogForm =  formFactory.form(RequestBlog.class).bindFromRequest(request);
        if(requestBlogForm.hasErrors())
            return badRequest("Error in form data.");
        RequestBlog requestBlog = requestBlogForm.get();
        String result = requestBlog.validate();
        if (!result.equals("valid"))
            return badRequest(result);
        if(blogRepository.findBlogByTitle(requestBlog.getTitle()) != null)
            return badRequest("BLOG ALREADY EXISTS WITH TITLE: "+requestBlog.getTitle());
        List<String> imagePaths = saveImagesAndGetPath(request, requestBlog.getTitle());
        if(imagePaths.isEmpty())
            return badRequest("Images not found");
        requestBlog.setImagePaths(imagePaths);
        MyBlog newBlog = requestBlog.getMyBlog(authorId);
        if(blogRepository.save(newBlog))
            return ok("Blog saved Successfully\n"+blogRepository.findBlogByTitle(newBlog.getTitle()));
        return internalServerError("Could not save blog.");
    }

    public Result deleteBlog(Integer userId, String blogTitle) {
        if (blogTitle.isBlank()) return badRequest("Enter Blog Title.");
        MyBlog blog = blogRepository.findBlogByTitle(blogTitle);
        if (blog!= null && blog.getAuthor().getId() == userId){
            if (blogRepository.delete(blog))
                return ok("Blog deleted successfully.\n"+blog);
            else internalServerError("Something went wrong");
        }
        return badRequest("Only author can delete it.");
    }

//    Method to save picture in assets folder with package name as Blog Title
    public List<String> saveImagesAndGetPath(Http.Request request, String blogTitle){
        List<String> imagesPath = new ArrayList<>();
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart<Files.TemporaryFile>> fileParts = body.getFiles();
        try {
            String dir = "app/assets/images/'" + blogTitle + "'/";
            Path filepath = Paths.get(dir);
            java.nio.file.Files.createDirectories(filepath);

            for (Http.MultipartFormData.FilePart<Files.TemporaryFile> picture : fileParts) {
                String fileName = picture.getFilename();
                Files.TemporaryFile file = picture.getRef();
                Path destinationPath = Paths.get(dir + fileName);
                file.copyTo(destinationPath, true);
                imagesPath.add(String.valueOf(destinationPath));
                logger.info("File Uploaded Successfully.");
            }
        }catch (IOException ex) {
            logger.warn(ex.getMessage());
        }
        return imagesPath;
    }

}
