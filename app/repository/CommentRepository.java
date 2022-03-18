package repository;

import models.MyComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.xml.stream.events.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    private static CommentRepository instance = null;
    private final Connection connection;
    private final Logger logger;

    String TABLE_NAME = "BLOG_COMMENTS";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "COMMENT_ID INTEGER AUTO_INCREMENT, "+
            "COMMENT varchar(200) NOT NULL, "+
            "CREATION_TIME varchar(200) NOT NULL, "+
            "BLOG_ID INTEGER, "+
            "PRIMARY KEY (COMMENT_ID)," +
            "FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID))";

    private CommentRepository() {
        logger = LoggerFactory.getLogger(CommentRepository.class);
        connection = MyDatabase.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyComment comment) {
        try {
            String saveQuery = "INSERT INTO "+TABLE_NAME+" (COMMENT, CREATION_TIME, BLOG_ID) VALUES(?,?,?);";
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1,comment.getComment());
            statement.setString(2, comment.getTimestamp());
            statement.setInt(3, comment.getBlog().getId());
            int count = statement.executeUpdate();
            logger.info(count+" Comment saved successfully.");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyComment findCommentById(Integer id) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE COMMENT_ID=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery();
            statement.setInt(1, id);
            if(resultSet.next()) {
                return MyComment.newBuilder()
                        .setId(resultSet.getInt("COMMENT_ID"))
                        .setComment(resultSet.getString("COMMENT"))
                        .setTimestamp(resultSet.getString("CREATION_TIME"))
                        .setBlog(BlogRepository.getInstance().findBlogById(resultSet.getInt("BLOG_ID")))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public List<MyComment> findAllComments() {
        List<MyComment> comments = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM " + TABLE_NAME;
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

        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return comments;
    }

    public boolean deleteCommentByBlogTitle(Integer blogId){
        try {
            String query = "DELETE FROM "+ TABLE_NAME+" WHERE BLOG_ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, blogId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteComment(MyComment comment){
        try {
            String query = "DELETE FROM "+ TABLE_NAME+" WHERE COMMENT_ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, comment.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
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
}
