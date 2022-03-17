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

    public boolean save(@NotNull MyBlog blog) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("INSERT INTO "+TABLE_NAME+ " (BLOG_TITLE, BLOG_CONTENT, CREATION_TIME, AUTHOR_ID) VALUES (?,?,?,?)");
        stm.setString(1, blog.getTitle());
        stm.setString(2, blog.getContent());
        stm.setString(3, blog.getTimestamp());
        stm.setInt(4, blog.getAuthor().getId());
        int count = stm.executeUpdate();
        logger.info("Blog saved successfully");
        System.out.println(stm);
        MyBlog savedBlog = findBlogByTitle(blog.getTitle());
        return (count == 1 && ImageRepository.getInstance().save(savedBlog));
    }

    public MyBlog findBlogByTitle(@NotEmpty @NotNull String title) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_TITLE=?";
        PreparedStatement stm = connection.prepareStatement(findQuery);
        stm.setString(1, title);
        System.out.println(stm);
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
        return null;
    }

    public MyBlog findBlogById(@NotNull Integer blogId) throws SQLException {
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
        return null;
    }

    public List<MyBlog> findAllBlogs() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        PreparedStatement smt = connection.prepareStatement(findQuery);
        List<MyBlog> blogs = new ArrayList<>();
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
        return blogs;
    }

    public boolean updateBlog(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog) throws SQLException {
        String query = "UPDATE "+ TABLE_NAME +" SET BLOG_TITLE=?, BLOG_CONTENT=?, AUTHOR_ID=? WHERE BLOG_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        logger.info(statement.toString());
        int count = statement.executeUpdate();
        return (count == 1 && ImageRepository.getInstance().updateImagePath(oldBlog, newBlog));
    }

    public boolean delete(@NotNull MyBlog blog) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        logger.info(statement.toString());
        int count = statement.executeUpdate();
        return (count == 1 && ImageRepository.getInstance().deleteAllImagePaths(blog));
    }

    public static BlogRepository getInstance(){
        if(instance == null){
            instance = new BlogRepository();
        }
        return instance;
    }
}
