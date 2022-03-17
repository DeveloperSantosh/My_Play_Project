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

    public boolean save(@NotNull MyUser user) throws SQLException {
        int count = 0;
        String query = "INSERT INTO "+TABLE_NAME+" VALUES(?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        for(MyRole role: user.getRoleList()) {
            statement.setString(1, role.getRoleType());
            statement.setInt(2, user.getId());
            if(!RoleRepository.getInstance().findAllUserRoles().contains(role))
                RoleRepository.getInstance().save(role);
            count += statement.executeUpdate();
        }
        logger.info("User saved successfully");
        return (count >= 1);
    }

    public List<MyUser> findUsersByRoleType(String roleType) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE=?";
        PreparedStatement statement = connection.prepareStatement(findQuery);
        statement.setString(1, roleType);
        List<MyUser> users = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()) {
            users.add(UserRepository.getInstance().findUserByID(resultSet.getInt("USER_ID")));
        }
        return users;
    }

    public List<MyRole> findRolesByUserId(int userId) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID = ?";
        PreparedStatement statement = connection.prepareStatement(findQuery);
        statement.setInt(1, userId);
        List<MyRole> userRoles = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            userRoles.add(RoleRepository.getInstance().
                    findUserRoleByType(resultSet.getString("ROLE_TYPE")));
        }
        return userRoles;
    }

    public boolean updateUserRole(MyUser oldUser, MyUser newUser) throws SQLException {
        return deleteUserAllRoles(oldUser) && save(newUser);
    }

    public boolean deleteUserRole(MyUser user, MyRole role) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE =? AND USER_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, role.getRoleType());
        statement.setInt(2, user.getId());
        int count = statement.executeUpdate();
        return (count == 1);
    }

    public boolean deleteUserAllRoles(MyUser user) throws SQLException {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            int count = statement.executeUpdate();
        return (count >= 0);
    }
    public static UserRoleRepository getInstance(){
        if(instance == null){
            instance = new UserRoleRepository();
        }
        return instance;
    }
}
