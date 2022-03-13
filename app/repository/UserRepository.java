package repository;

import models.User;
import play.db.Database;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TABLE_NAME = "MY_USER";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "USER_ID INTEGER AUTO_INCREMENT, "+
            "USERNAME varchar(200) NOT NULL, "+
            "PASSWORD varchar(200) NOT NULL, "+
            "EMAIL varchar(200) UNIQUE, "+
            "PRIMARY KEY (USER_ID))";
    Statement statement = null;
    private static UserRepository instance = null;

    private UserRepository() {
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

    public boolean save(@NotNull User user) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (USERNAME, PASSWORD, EMAIL) VALUES ('"+
                user.getUsername()+"','"+
                user.getPassword()+"','"+
                user.getEmail()+"');";
        int count = statement.executeUpdate(saveQuery);
        System.out.println(saveQuery);
        return (count == 1 && UserRoleRepository.getInstance().save(user));
    }

    public User findUserByID(Integer id) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID="+id;
        User user = new User();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            user.setId(resultSet.getInt("USER_ID"));
            user.setUsername(resultSet.getString("USERNAME"));
            user.setPassword(resultSet.getString("PASSWORD"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setRoles(UserRoleRepository.getInstance().findRolesByUserId(user.getId()));
            return user;
        }
        return null;
    }

    public User findUserByName(String name) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USERNAME='"+name+"';";
        User user = new User();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            user.setId(resultSet.getInt("USER_ID"));
            user.setUsername(resultSet.getString("USERNAME"));
            user.setPassword(resultSet.getString("PASSWORD"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setRoles(UserRoleRepository.getInstance().findRolesByUserId(user.getId()));
            return user;
        }
        return null;
    }

    public User findUserByEmail(String email) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE EMAIL = '"+email+"';";
        User user = new User();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            user.setId(resultSet.getInt("USER_ID"));
            user.setUsername(resultSet.getString("USERNAME"));
            user.setPassword(resultSet.getString("PASSWORD"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setRoles(UserRoleRepository.getInstance().findRolesByUserId(user.getId()));
            return user;
        }
        return null;
    }

    public List<User> findAllUsers() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<User> users = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt("USER_ID"));
            user.setUsername(resultSet.getString("USERNAME"));
            user.setPassword(resultSet.getString("PASSWORD"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setRoles(UserRoleRepository.getInstance().findRolesByUserId(user.getId()));
            users.add(user);
        }
        return users;
    }

    public boolean updateUser(User oldUser, User newUser) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "USERNAME = '"+newUser.getUsername()+"',"+
                "PASSWORD = '"+newUser.getPassword()+"',"+
                "EMAIL = '"+newUser.getEmail()+"' " +
                "WHERE USER_ID="+oldUser.getId();
        UserRoleRepository.getInstance().updateUserRole(oldUser, newUser);
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(User user) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID="+user.getId();
        UserRoleRepository.getInstance().deleteUserAllRoles(user);
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
}
