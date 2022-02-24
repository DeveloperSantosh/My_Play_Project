package controllers;

import models.Blog;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogSQLController {
    Connection dbConn;
    final String DB_URL = "jdbc:mysql://localhost:3306/MyDB";
    final String USER = "root";
    final String PASS = "root";

    public BlogSQLController(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // throws ClassNotFoundException
            dbConn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected.");
            Statement stmt = dbConn.createStatement();
            String query = "CREATE TABLE blog (" +
                    "title VARCHAR(200) NOT NULL," +
                    "content VARCHAR(500) NOT NULL, " +
                    "timestamp VARCHAR(100) NOT NULL,"+
                    "FK_user_id INTEGER NOT NULL," +
                    "PRIMARY KEY(title)" +
                    ")";
            boolean r = stmt.execute(query);
            if(r) System.out.println("Blog Table Created Successfully");
            else System.out.println("could not create blog table");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("From Blog:"+e.getMessage());
        }
    }

    public void insertBlog(Blog blog) {
        try {
            Statement stmt = dbConn.createStatement();
            int count = stmt.executeUpdate("Insert into blog(title, content, timestamp, FK_author_id) values ('"+
                    blog.getTitle() + "','" + blog.getContent() + "','" +
                    blog.getTimestamp()+"','"+ blog.getAuthor().getId()+"');");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteBlog(Blog blog) {
        try {
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate("delete from blog where title ='"+blog.getTitle()+"';");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateBlog(String title, Blog newBlog) {
        try {
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate("update blog set"+
                    "title ='" + newBlog.getTitle()+"','"+
                    "content ='" + newBlog.getContent()+"','"+
                    "timestamp ='" + newBlog.getTimestamp()+"','"+
                    "FK_user_id ='"+ newBlog.getAuthor().getId()+
                    "where title ='"+title+"';");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Blog> retrieveBlogs() {
        List<Blog> userList = new ArrayList<>();
        try {
            Statement stmt = dbConn.createStatement();
            ResultSet r = stmt.executeQuery("Select * from blog;");
            while(r.next()){
                Blog dbBlog = new Blog();
                dbBlog.setTitle(r.getString("title"));
                dbBlog.setContent(r.getString("content"));
                dbBlog.setTimestamp(r.getString("timestamp"));
                int author_id = r.getInt("FK_user_id");
                ResultSet authorResultSet = stmt.executeQuery("Select * from user where id ="+author_id);
                while(authorResultSet.next()){
                    User author = authorResultSet.getObject(1,User.class);
                    dbBlog.setAuthor(author);
                }
                userList.add(dbBlog);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    public Blog retrieveBlogByTitle(String title){
        try {
            Blog blog = new Blog();
            Statement stmt = dbConn.createStatement();
            ResultSet r = stmt.executeQuery("Select * from blog where title = '"+title+"';");
            blog.setTitle(r.getString("title"));
            blog.setContent(r.getString("content"));
            blog.setTimestamp(r.getString("timestamp"));
            int user_id = r.getInt("id");
            ResultSet userResultSet = stmt.executeQuery("Select * from user where id = "+user_id+";");
            while(userResultSet.next()){
                User author = userResultSet.getObject(1, User.class);
                blog.setAuthor(author);
            }
            return blog;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
