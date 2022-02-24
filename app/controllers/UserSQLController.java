package controllers;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserSQLController {
    Connection dbConn;
    final String DB_URL = "jdbc:mysql://localhost:3306/MyDB";
    final String USER = "root";
    final String PASS = "root";

    public UserSQLController(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // throws ClassNotFoundException
            dbConn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected.");
            Statement stmt = dbConn.createStatement();
            String query = "Create Table user (" +
                    "id INTEGER NOT NULL ," +
                    "name VARCHAR(100) NOT NULL, " +
                    "surname VARCHAR(100) NOT NULL," +
                    "PRIMARY KEY(id));";
            boolean r = stmt.execute(query);
            if(r) System.out.println("User Table Created Successfully");
            else System.out.println("Could not create User Table");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("From User:"+e.getMessage());
        }
    }

    public void insertUser(User user) {
        try {
            Statement stmt = dbConn.createStatement();
            String query = "INSERT INTO user (id, name, surname) VALUES ("+
                    user.getId() + ", '" +
                    user.getName() + "', '" +
                    user.getSurname()+"')";
            stmt.executeUpdate(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        try {
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate("delete from user where id ="+user.getId()+";");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateUser(int id, User newUser) {
        try {
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate("update user set "+
                    "name ='" + newUser.getName()+"',"+
                    "surname ='" + newUser.getSurname()+"' "+
                    "where id ="+id+";");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<User> retrieveUsers() {
        List<User> userList = new ArrayList<>();
        try {
            Statement stmt = dbConn.createStatement();
            ResultSet r = stmt.executeQuery("Select * from user");
            int i=1;
            while(r.next()){
                User dbUser = new User();
//                User dbUser = r.getObject(i, User.class);
                dbUser.setId(r.getInt("id"));
                dbUser.setName(r.getString("name"));
                dbUser.setSurname(r.getString("surname"));
                userList.add(dbUser);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    public User retrieveUserById(int id){
        try {
            User user = new User();
            Statement stmt = dbConn.createStatement();
            ResultSet r = stmt.executeQuery("Select * from user where id = "+id+";");
            while(r.next()){
                user.setId(r.getInt("id"));
                user.setName(r.getString("name"));
                user.setSurname(r.getString("surname"));
            }
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
