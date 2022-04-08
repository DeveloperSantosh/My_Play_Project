package service;

import exception.UserNotFoundException;
import models.MyBlog;
import dto.RequestBlog;
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
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public Result showAllBlogs(){
        StringBuilder result = new StringBuilder();
        for(MyBlog blog: blogRepository.findAllBlogs()){
            result.append(blog.toString()).append("\n");
        }
        return ok(result.toString());
    }

//    Method to retrieve blog from database with given keyword
    public Result showBlogByKeyword(String keyword) {
        if (keyword.isBlank() || keyword.isEmpty()) return badRequest("Keyword cannot be empty");
        List<MyBlog> blogs = blogRepository.findAllBlogs().stream()
                .filter(blog-> blog.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                blog.getAuthor().getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                blog.getAuthor().getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                blog.getContent().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
        StringBuilder result = new StringBuilder();
        blogs.forEach(blog->result.append(blog.toString()).append("\n"));
        return ok(result.toString());
    }

    public Result showMyBlogs(Http.Request request) throws UserNotFoundException{
        String email = request.session().get("email")
                .orElseThrow(()->new UserNotFoundException("Sorry! user not found in session"));
        List<MyBlog> myBlogs = BlogRepository.getInstance().findAllBlogs().stream()
                        .filter(blog->blog.getAuthor().getEmail().equals(email))
                        .collect(Collectors.toList());
        StringBuilder result = new StringBuilder();
        for (MyBlog blog: myBlogs){
            result.append(blog).append("\n");
        }
        return ok(result.toString());
    }

//    Method to save blog to database
    public Result saveBlog(Http.Request request){
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
        int authorId = UserRepository.getInstance().findUserByEmail(request.session().get("email")
                .orElseThrow(()-> new UserNotFoundException("User session not found"))).getId();
        MyBlog newBlog = requestBlog.getMyBlog(authorId);
        if(blogRepository.save(newBlog))
            return ok("Blog saved Successfully\n"+blogRepository.findBlogByTitle(newBlog.getTitle()));
        return internalServerError("Something went wrong.");
    }

    public Result deleteBlogByTitle(String blogTitle, Http.Request request) {
        if (blogTitle.isBlank()) return badRequest("Enter Blog Title.");
        MyBlog blog = blogRepository.findBlogByTitle(blogTitle);
        int userId;
        if (request.session().get("email").isPresent()){
            userId = UserRepository.getInstance().findUserByEmail(request.session().get("email").get()).getId();
        }
        else return unauthorized("No user session found");
        if (blog!= null && blog.getAuthor().getId() == userId){
            if (blogRepository.delete(blog))
                return ok("Blog deleted successfully.\n"+blog);
            else internalServerError("Something went wrong");
        }
        return badRequest("Only author can delete it.");
    }

    public Result updateBlog(Http.Request request) {
        Form<RequestBlog> requestBlogForm =  formFactory.form(RequestBlog.class).bindFromRequest(request);
        if(requestBlogForm.hasErrors())
            return badRequest("Error in form data.");
        RequestBlog requestBlog = requestBlogForm.get();
        String result = requestBlog.validate();
        if (!result.equals("valid"))
            return badRequest(result);
        MyBlog oldBlog = blogRepository.findBlogByTitle(requestBlog.getTitle());
        if( oldBlog== null)
            return notFound("Blog Not Found with title: "+requestBlog.getTitle()+" to update");
        int authorId = UserRepository.getInstance().findUserByEmail(request.session().get("email")
                .orElseThrow(()-> new UserNotFoundException("Sorry! User Session not Found."))).getId();
        if (oldBlog.getAuthor().getId() != authorId)
            return forbidden("Only author can modify this blog");
        List<String> imagePaths = saveImagesAndGetPath(request, requestBlog.getTitle());
        if(imagePaths.isEmpty())
            return badRequest("No Images found in blog.");
        requestBlog.setImagePaths(imagePaths);
        MyBlog newBlog = requestBlog.getMyBlog(authorId);
        if(blogRepository.updateBlog(oldBlog, newBlog))
            return ok("Blog Updated Successfully\n"+blogRepository.findBlogByTitle(newBlog.getTitle()));
        return internalServerError("Could not save blog.");
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
