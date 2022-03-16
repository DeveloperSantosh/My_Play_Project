package repository;

import models.MyBlog;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BlogRepository {
    String TABLE_NAME = "MY_BLOGS";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "BLOG_ID INTEGER AUTO_INCREMENT, "+
            "BLOG_TITLE varchar(200) NOT NULL, "+
            "BLOG_CONTENT text NOT NULL, "+
            "CREATION_TIME varchar(200) NOT NULL, "+
            "AUTHOR_ID INTEGER, "+
            "PRIMARY KEY (BLOG_ID)," +
            "FOREIGN KEY (AUTHOR_ID) REFERENCES MY_USER(USER_ID))";
    Statement statement = null;
    private static BlogRepository instance = null;

    private BlogRepository() {
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

    public boolean save(@NotNull MyBlog blog) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (BLOG_TITLE, BLOG_CONTENT, CREATION_TIME, AUTHOR_ID) VALUES ('"+
                blog.getTitle()+"','"+
                blog.getContent()+"','"+
                blog.getTimestamp()+"',"+
                blog.getAuthor().getId()+")";
        System.out.println(saveQuery);
        int count = statement.executeUpdate(saveQuery);

        return (count == 1);
    }

    public MyBlog findBlogByTitle(String title) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_TITLE = '"+title+"'";
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            return MyBlog.newBuilder()
                    .setId(resultSet.getInt("BLOG_ID"))
                    .setTitle(resultSet.getString("BLOG_TITLE"))
                    .setContent(resultSet.getString("BLOG_CONTENT"))
                    .setTimestamp(resultSet.getString("CREATION_TIME"))
                    .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                    .build();
        }
        return null;
    }

    public MyBlog findBlogById(Integer id) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID = "+id;
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            return MyBlog.newBuilder()
                    .setId(resultSet.getInt("BLOG_ID"))
                    .setTitle(resultSet.getString("BLOG_TITLE"))
                    .setContent(resultSet.getString("BLOG_CONTENT"))
                    .setTimestamp(resultSet.getString("CREATION_TIME"))
                    .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                    .build();
        }
        return null;
    }

    public List<MyBlog> findAllBlogs() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<MyBlog> blogs = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            MyBlog blog = MyBlog.newBuilder()
                    .setId(resultSet.getInt("BLOG_ID"))
                    .setTitle(resultSet.getString("BLOG_TITLE"))
                    .setContent(resultSet.getString("BLOG_CONTENT"))
                    .setTimestamp(resultSet.getString("CREATION_TIME"))
                    .setAuthor(UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID")))
                    .build();
            blogs.add(blog);
        }
        return blogs;
    }

    public boolean updateBlog(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "BLOG_TITLE = '"+newBlog.getTitle()+"',"+
                "BLOG_CONTENT = '"+newBlog.getContent()+"',"+
                "AUTHOR_ID  = "+newBlog.getAuthor().getId()+"' " +
                "WHERE BLOG_ID="+oldBlog.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(@NotNull MyBlog blog) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID="+blog.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static BlogRepository getInstance(){
        if(instance == null){
            instance = new BlogRepository();
        }
        return instance;
    }
}
