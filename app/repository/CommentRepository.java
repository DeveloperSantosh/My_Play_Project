package repository;

import models.MyComment;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    String TABLE_NAME = "BLOG_COMMENTS";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "COMMENT_ID INTEGER AUTO_INCREMENT, "+
            "COMMENT varchar(200) NOT NULL, "+
            "CREATION_TIME varchar(200) NOT NULL, "+
            "BLOG_ID INTEGER, "+
            "PRIMARY KEY (COMMENT_ID)," +
            "FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID))";
    Statement statement = null;
    private static CommentRepository instance = null;

    private CommentRepository() {
        Connection connection = MyDatabase.getConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate(createTable);
            System.out.println(createTable);
            System.out.println("Table fetched successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean save(@NotNull MyComment comment) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (COMMENT, CREATION_TIME, BLOG_ID) VALUES ('"+
                comment.getComment()+"','"+
                comment.getTimestamp()+"',"+
                comment.getBlog().getId()+ ")";
        System.out.println(saveQuery);
        int count = statement.executeUpdate(saveQuery);
        return (count == 1);
    }

    public MyComment findCommentById(Integer id) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE COMMENT_ID = "+id;
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            return MyComment.newBuilder()
                    .setId(resultSet.getInt("COMMENT_ID"))
                    .setComment(resultSet.getString("COMMENT"))
                    .setTimestamp(resultSet.getString("CREATION_TIME"))
                    .setBlog(BlogRepository.getInstance().findBlogById(resultSet.getInt("BLOG_ID")))
                    .build();
        }
        return null;
    }

    public List<MyComment> findAllComments() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<MyComment> comments = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            MyComment comment = MyComment.newBuilder()
                    .setId(resultSet.getInt("COMMENT_ID"))
                    .setComment(resultSet.getString("COMMENT"))
                    .setTimestamp(resultSet.getString("CREATION_TIME"))
                    .setBlog(BlogRepository.getInstance().findBlogById(resultSet.getInt("BLOG_ID")))
                    .build();
            comments.add(comment);
        }
        return comments;
    }

    public static CommentRepository getInstance(){
        if(instance == null){
            instance = new CommentRepository();
        }
        return instance;
    }
}
