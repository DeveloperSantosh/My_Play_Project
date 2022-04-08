package repository;

import models.MyBlog;
import models.MyComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogRepository {

    private final String TABLE_NAME = "MY_BLOGS";
    private static BlogRepository instance = null;
    private final Logger logger = LoggerFactory.getLogger(BlogRepository.class);

    private BlogRepository() {}

    public boolean save(@NotNull MyBlog blog) {
        if (validateBlog(blog)) return false;
        String insertQuery = "INSERT INTO "+TABLE_NAME+" (BLOG_TITLE, BLOG_CONTENT, CREATION_TIME, AUTHOR_ID) VALUES (?,?,?,?)";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
                insertStatement.setString(1, blog.getTitle());
                insertStatement.setString(2, blog.getContent());
                insertStatement.setString(3, blog.getTimestamp());
                insertStatement.setInt(4, blog.getAuthor().getId());
                if (insertStatement.executeUpdate()==1) {
                    int savedBlogId = findBlogByTitle(blog.getTitle()).getId();
                    if(ImageRepository.getInstance().save(blog, savedBlogId))
                        connection.commit();
                    logger.info("Blog saved successfully");
                    return true;
                }
            }catch (SQLException e){
                logger.warn(e.getMessage());
            }
            connection.rollback(savepoint);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyBlog findBlogByTitle(@NotEmpty @NotNull String title) {
        MyBlog blog = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_TITLE=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement stm = connection.prepareStatement(findQuery)){
            stm.setString(1, title);
            ResultSet resultSet = stm.executeQuery();
            if(resultSet.next()) {
                int blogId = resultSet.getInt("BLOG_ID");
                blog = MyBlog.newBuilder()
                        .setId(blogId)
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogId(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .addAllComments(CommentRepository.getInstance().findCommentsByBlogId(blogId))
                        .build();
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return blog;
    }

    public MyBlog findBlogById(@NotNull Integer blogId) {
        MyBlog blog = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID = ?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement smt = connection.prepareStatement(findQuery)){
            smt.setInt(1, blogId);
            ResultSet resultSet = smt.executeQuery();
            if(resultSet.next()) {
                blog = MyBlog.newBuilder()
                        .setId(resultSet.getInt("BLOG_ID"))
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogId(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .build();
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return blog;
    }

    public List<MyBlog> findAllBlogs() {
        List<MyBlog> blogs = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement smt = connection.prepareStatement(findQuery);
            ResultSet resultSet = smt.executeQuery()){
            while(resultSet.next()) {
                int blogId = resultSet.getInt("BLOG_ID");
                MyBlog blog = MyBlog.newBuilder()
                        .setId(blogId)
                        .setTitle(resultSet.getString("BLOG_TITLE"))
                        .setContent(resultSet.getString("BLOG_CONTENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .addAllImagePath(ImageRepository.getInstance().findImagesPathByBlogId(blogId))
                        .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                        .addAllComments(CommentRepository.getInstance().findCommentsByBlogId(blogId))
                        .build();
                blogs.add(blog);
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return blogs;
    }

    public boolean updateBlog(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog){
        if(!(validateBlog(newBlog) && validateBlog(oldBlog))) return false;
        String updateQuery = "UPDATE " + TABLE_NAME + " SET BLOG_TITLE=?, BLOG_CONTENT=?, AUTHOR_ID=? WHERE BLOG_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            Savepoint savepoint = connection.setSavepoint();
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)){
                statement.executeUpdate();
                if (ImageRepository.getInstance().updateImagePath(oldBlog, newBlog)) {
                    connection.commit();
                    return true;
                }
                else connection.rollback(savepoint);
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(@NotNull MyBlog blog){
        if (!validateBlog(blog)) return false;
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Savepoint savepoint = connection.setSavepoint();
            boolean isCommentsDeleted = CommentRepository.getInstance().deleteAllCommentByBlogTitle(blog.getId());
            boolean isImagesDeleted = ImageRepository.getInstance().deleteAllImagePaths(blog);
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, blog.getId());
                if (isImagesDeleted && isCommentsDeleted && statement.executeUpdate()==1){
                    ImageRepository.getInstance().deleteImageFiles(blog.getImagePathList());
                    connection.commit();
                    return true;
            }
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }
            connection.rollback(savepoint);
            if (isCommentsDeleted)
                for (MyComment c: blog.getCommentsList())
                    CommentRepository.getInstance().save(c);
            if (isImagesDeleted)
                ImageRepository.getInstance().save(blog, blog.getId());
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

    public boolean validateBlog(MyBlog blog){
        if (blog.getTitle().isBlank() || blog.getTitle().length()>100 )
            return false;
        if (blog.getContent().isBlank() || blog.getContent().length() >500)
            return false;
        if (blog.getImagePathCount()<1)
            return false;
        return true;
    }


//    private void createTable(){
//        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
//                "BLOG_ID INTEGER AUTO_INCREMENT, "+
//                "BLOG_TITLE varchar(200) NOT NULL, "+
//                "BLOG_CONTENT text NOT NULL, "+
//                "CREATION_TIME varchar(200) NOT NULL, "+
//                "AUTHOR_ID INTEGER NOT NULL, "+
//                "PRIMARY KEY (BLOG_ID)," +
//                "FOREIGN KEY (AUTHOR_ID) REFERENCES MY_USER(USER_ID))";
//
//        try (Connection connection = MyDatabase.getConnection();
//            PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)){
//            if (createTableStatement.execute())
//                logger.info("Table Created Successfully");
//        } catch (SQLException e) {
//            logger.warn(e.getMessage());
//        }
//
//    }
}
