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
                    "id Integer NOT NULL," +
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

    public boolean insertUser(User user) {
        try {
            Statement stmt = dbConn.createStatement();
            return stmt.execute("Insert into User(id, name, surname) values ("+
                    user.getId() + ", '" + user.getName() + "' , '" + user.getSurname()+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(User user) {
        try {
            Statement stmt = dbConn.createStatement();
            return stmt.execute("delete from User where id ="+user.getId()+";");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(int id, User newUser) {
        try {
            Statement stmt = dbConn.createStatement();
            return stmt.execute("update user set"+
                    "id =" + newUser.getId()+","+
                    "name ='" + newUser.getName()+"',"+
                    "surname ='" + newUser.getSurname()+"',"+
                    "where id ="+id+";");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<User> retrieveUsers() {
        List<User> userList = new ArrayList<>();
        try {
            Statement stmt = dbConn.createStatement();
            ResultSet r = stmt.executeQuery("Select * from user");
            int i=1;
            while(r.next()){
                User dbUser = r.getObject(i, User.class);
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
                user = r.getObject(1, User.class);
            }
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
