package repository;

import models.MyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static UserRepository instance = null;
    private final Connection connection;
    private final Logger logger;


    private static final String TABLE_NAME = "MY_USER";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "USER_ID INTEGER AUTO_INCREMENT, "+
            "USERNAME varchar(200) NOT NULL, "+
            "PASSWORD varchar(200) NOT NULL, "+
            "EMAIL varchar(200) UNIQUE, "+
            "PRIMARY KEY (USER_ID))";

    private UserRepository() {
        connection = MyDatabase.getConnection();
        logger = LoggerFactory.getLogger(BlogRepository.class);
        try {
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyUser user) {
        try {
            String saveQuery = "INSERT INTO "+TABLE_NAME+" VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.executeUpdate();
            logger.info("User saved successfully");
            return UserRoleRepository.getInstance().save(user)  &&
                   UserPermissionRepository.getInstance().save(user);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyUser findUserByID(@NotNull Integer id) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public MyUser findUserByName(String name) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USERNAME=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                int id = resultSet.getInt("USER_ID");
                return MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public MyUser findUserByEmail(String email) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE EMAIL = ?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                int id = resultSet.getInt("USER_ID");
                return MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public List<MyUser> findAllUsers() {
        List<MyUser> users = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME;
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("USER_ID");
                MyUser user = MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return users;
    }

    public boolean updateUser(MyUser oldUser, MyUser newUser) {
        try {
            String query = "UPDATE "+TABLE_NAME+" SET USERNAME=?, PASSWORD=?, EMAIL=? WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newUser.getUsername());
            statement.setString(2, newUser.getPassword());
            statement.setString(3, newUser.getEmail());
            UserRoleRepository.getInstance().updateUserRole(oldUser, newUser);
            int count = statement.executeUpdate();
            logger.info("TOTAL USER UPDATED: "+count);
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(MyUser user) {
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            UserRoleRepository.getInstance().deleteUserAllRoles(user);
            UserPermissionRepository.getInstance().deleteUserAllPermission(user);
            int count = statement.executeUpdate();
            logger.info("TOTAL USER DELETED: "+count);
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
}
