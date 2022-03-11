package repository;

import model.Blog;
import model.User;
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

    public boolean save(@NotNull Blog blog) throws SQLException {
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

    public Blog findBlogByTitle(String title) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_TITLE = '"+title+"'";
        Blog blog = new Blog();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            blog.setBlogId(resultSet.getInt("BLOG_ID"));
            blog.setTitle(resultSet.getString("BLOG_TITLE"));
            blog.setContent(resultSet.getString("BLOG_CONTENT"));
            blog.setTimestamp(resultSet.getString("CREATION_TIME"));
            User author = UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID"));
            blog.setAuthor(author);
            return blog;
        }
        return null;
    }

    public Blog findBlogById(Integer id) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID = "+id;
        Blog blog = new Blog();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            blog.setBlogId(resultSet.getInt("BLOG_ID"));
            blog.setTitle(resultSet.getString("BLOG_TITLE"));
            blog.setContent(resultSet.getString("BLOG_CONTENT"));
            blog.setTimestamp(resultSet.getString("CREATION_TIME"));
            User author = UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID"));
            blog.setAuthor(author);
            return blog;
        }
        return null;
    }

    public List<Blog> findAllBlogs() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<Blog> blogs = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            Blog blog = new Blog();
            blog.setBlogId(resultSet.getInt("BLOG_ID"));
            blog.setTitle(resultSet.getString("BLOG_TITLE"));
            blog.setContent(resultSet.getString("BLOG_CONTENT"));
            blog.setTimestamp(resultSet.getString("CREATION_TIME"));
            User author = UserRepository.getInstance().findUserByID(resultSet.getInt("AUTHOR_ID"));
            blog.setAuthor(author);
            blogs.add(blog);
        }
        return blogs;
    }

    public boolean updateBlog(@NotNull Blog oldBlog, @NotNull Blog newBlog) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "BLOG_TITLE = '"+newBlog.getTitle()+"',"+
                "BLOG_CONTENT = '"+newBlog.getContent()+"',"+
                "AUTHOR_ID  = "+newBlog.getAuthor().getId()+"' " +
                "WHERE BLOG_ID="+oldBlog.getBlogId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(@NotNull Blog blog) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID="+blog.getBlogId();
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
