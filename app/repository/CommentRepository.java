package repository;

import models.MyComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    private final Logger logger = LoggerFactory.getLogger(CommentRepository.class);
    private static CommentRepository instance = null;
    private final String TABLE_NAME = "BLOG_COMMENTS";

    private CommentRepository() {
        createTable();
    }

    private void createTable(){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "COMMENT_ID INTEGER AUTO_INCREMENT, "+
                "COMMENT varchar(200) NOT NULL, "+
                "CREATION_TIME varchar(200) NOT NULL, "+
                "BLOG_ID INTEGER, "+
                "PRIMARY KEY (COMMENT_ID)," +
                "FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID))";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(createTableQuery);
            if (statement.execute())
                logger.info("Table created successfully.");
            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyComment comment) {
        if (validateComment(comment)) return false;
        String saveQuery = "INSERT INTO "+TABLE_NAME+" (COMMENT, CREATION_TIME, BLOG_ID) VALUES(?,?,?);";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1,comment.getComment());
            statement.setString(2, comment.getTimestamp());
            statement.setInt(3, comment.getBlog().getId());
            int count = statement.executeUpdate();
            statement.close();
            connection.close();
            logger.info(count+" Comment saved successfully.");
            return true;
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyComment> findCommentsByBlogId(Integer blogId) {
        List<MyComment> comments = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setInt(1, blogId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                comments.add(
                 MyComment.newBuilder()
                        .setId(resultSet.getInt("COMMENT_ID"))
                        .setComment(resultSet.getString("COMMENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
//                        .setBlog(BlogRepository.getInstance().findBlogById(resultSet.getInt("BLOG_ID")))
                        .build());
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return comments;
    }

    public List<MyComment> findAllComments() {
        List<MyComment> comments = new ArrayList<>();
        String findQuery = "SELECT * FROM " + TABLE_NAME;
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                MyComment comment = MyComment.newBuilder()
                        .setId(resultSet.getInt("COMMENT_ID"))
                        .setComment(resultSet.getString("COMMENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .setBlog(BlogRepository.getInstance().findBlogById(resultSet.getInt("BLOG_ID")))
                        .build();
                comments.add(comment);
            }
            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return comments;
    }

    public boolean deleteCommentByBlogTitle(Integer blogId){
        String query = "DELETE FROM "+ TABLE_NAME+" WHERE BLOG_ID=?;";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, blogId);
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteComment(MyComment comment){
        String query = "DELETE FROM "+ TABLE_NAME+" WHERE COMMENT_ID=?;";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, comment.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (SQLException |NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static CommentRepository getInstance(){
        if(instance == null){
            instance = new CommentRepository();
        }
        return instance;
    }

    public boolean validateComment(MyComment comment){
        return !comment.getComment().isBlank() && comment.getComment().length() <= 100;
    }
}
