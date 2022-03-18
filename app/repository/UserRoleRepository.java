package repository;

import models.MyUser;
import models.MyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRoleRepository {

    private static UserRoleRepository instance = null;
    private final Logger logger;
    Connection connection;

    String TABLE_NAME = "MY_USER_ROLE";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "ROLE_TYPE varchar(200) NOT NULL, "+
            "USER_ID INTEGER NOT NULL, "+
            "FOREIGN KEY (ROLE_TYPE) REFERENCES MY_ROLE(ROLE_TYPE), "+
            "FOREIGN KEY (USER_ID) REFERENCES MY_USER(USER_ID), "+
            "PRIMARY KEY (ROLE_TYPE, USER_ID))";

    private UserRoleRepository() {
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
            String query = "INSERT INTO "+TABLE_NAME+" VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            for(MyRole role: user.getRoleList()) {
                statement.setString(1, role.getRoleType());
                statement.setInt(2, user.getId());
                if(!RoleRepository.getInstance().findAllUserRoles().contains(role))
                    RoleRepository.getInstance().save(role);
                statement.executeUpdate();
            }
            logger.info("User saved successfully");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyUser> findUsersByRoleType(String roleType) {
        List<MyUser> users = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, roleType);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                users.add(UserRepository.getInstance().findUserByID(resultSet.getInt("USER_ID")));
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return users;
    }

    public List<MyRole> findRolesByUserId(int userId) {
        List<MyRole> userRoles = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID = ?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                userRoles.add(RoleRepository.getInstance().
                        findUserRoleByType(resultSet.getString("ROLE_TYPE")));
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return userRoles;
    }

    public boolean updateUserRole(MyUser oldUser, MyUser newUser) {
        return deleteUserAllRoles(oldUser) && save(newUser);
    }

    public boolean deleteUserRole(MyUser user, MyRole role) {
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE =? AND USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, role.getRoleType());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteUserAllRoles(MyUser user) {
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }
    public static UserRoleRepository getInstance(){
        if(instance == null){
            instance = new UserRoleRepository();
        }
        return instance;
    }
}
