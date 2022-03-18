package repository;

import models.MyBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogRepository {

    private static BlogRepository instance = null;
    private final Connection connection;
    private final Logger logger;

    String TABLE_NAME = "MY_BLOGS";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "BLOG_ID INTEGER AUTO_INCREMENT, "+
            "BLOG_TITLE varchar(200) NOT NULL, "+
            "BLOG_CONTENT text NOT NULL, "+
            "CREATION_TIME varchar(200) NOT NULL, "+
            "AUTHOR_ID INTEGER NOT NULL, "+
            "PRIMARY KEY (BLOG_ID)," +
            "FOREIGN KEY (AUTHOR_ID) REFERENCES MY_USER(USER_ID))";

    private BlogRepository() {
        connection = MyDatabase.getConnection();
        logger = LoggerFactory.getLogger(BlogRepository.class);
        try {
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            logger.info("Table fetched successfully");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyBlog blog) {
        try {
            String query = "INSERT INTO "+TABLE_NAME+" (BLOG_TITLE, BLOG_CONTENT, CREATION_TIME, AUTHOR_ID) VALUES (?,?,?,?)";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setString(1, blog.getTitle());
            stm.setString(2, blog.getContent());
            stm.setString(3, blog.getTimestamp());
            stm.setInt(4, blog.getAuthor().getId());
            stm.executeUpdate();
            logger.info("Blog saved successfully");
            int savedBlogId = findBlogByTitle(blog.getTitle()).getId();
            return ImageRepository.getInstance().save(blog, savedBlogId);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyBlog findBlogByTitle(@NotEmpty @NotNull String title) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_TITLE=?";
            PreparedStatement stm = connection.prepareStatement(findQuery);
            stm.setString(1, title);
            ResultSet resultSet = stm.executeQuery();
            if(resultSet.next()) {
                int blogId = resultSet.getInt("BLOG_ID");
                return MyBlog.newBuilder()
                        .setId(blogId)
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogTitle(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public MyBlog findBlogById(@NotNull Integer blogId) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID = ?";
            PreparedStatement smt = connection.prepareStatement(findQuery);
            smt.setInt(1, blogId);
            ResultSet resultSet = smt.executeQuery();
            if(resultSet.next()) {
                return MyBlog.newBuilder()
                        .setId(resultSet.getInt("BLOG_ID"))
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogTitle(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public List<MyBlog> findAllBlogs() {
        List<MyBlog> blogs = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME;
            PreparedStatement smt = connection.prepareStatement(findQuery);
            ResultSet resultSet = smt.executeQuery();
            while(resultSet.next()) {
                int blogId = resultSet.getInt("BLOG_ID");
                MyBlog blog = MyBlog.newBuilder()
                        .setId(blogId)
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogTitle(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .build();
                blogs.add(blog);
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return blogs;
    }

    public boolean updateBlog(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog){
        try {
            String query = "UPDATE " + TABLE_NAME + " SET BLOG_TITLE=?, BLOG_CONTENT=?, AUTHOR_ID=? WHERE BLOG_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
            return ImageRepository.getInstance().updateImagePath(oldBlog, newBlog);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(@NotNull MyBlog blog){
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, blog.getId());
            return ImageRepository.getInstance().deleteAllImagePaths(blog) &&
                    CommentRepository.getInstance().deleteCommentByBlogTitle(blog.getId()) &&
                    statement.executeUpdate()==1;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static BlogRepository getInstance(){
        if(instance == null){
            instance = new BlogRepository();
        }
        return instance;
    }
}
