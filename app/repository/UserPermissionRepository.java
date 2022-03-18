package repository;

import models.MyPermission;
import models.MyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserPermissionRepository {

    private static UserPermissionRepository instance = null;
    private final Connection connection;
    private final Logger logger;

    private static final String TABLE_NAME = "MY_USER_PERMISSIONS";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "PERMISSION_ID INTEGER NOT NULL,"+
            "USER_ID INTEGER NOT NULL, "+
            "FOREIGN KEY (PERMISSION_ID) REFERENCES MY_PERMISSIONS (PERMISSION_ID), "+
            "FOREIGN KEY (USER_ID) REFERENCES MY_USER (USER_ID), "+
            "PRIMARY KEY (PERMISSION_ID, USER_ID))";

    private UserPermissionRepository() {
        connection = MyDatabase.getConnection();
        logger = LoggerFactory.getLogger(UserPermissionRepository.class);
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
            String saveQuery = "INSERT INTO "+ TABLE_NAME+ "VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            for(MyPermission p: user.getPermissionList()){
                statement.setInt(1, user.getId());
                statement.setInt(2, p.getId());
                if(!PermissionRepository.getInstance().findAllPermissions().contains(p))
                    PermissionRepository.getInstance().save(p);
                statement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyPermission> findAllPermissionsByUserId(int userId) {
        List<MyPermission> permissions = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int permissionId = resultSet.getInt("PERMISSION_ID");
                permissions.add(PermissionRepository.getInstance().findPermissionById(permissionId));
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return permissions;
    }


    public boolean deleteUserPermission(MyUser user, MyPermission permission) {
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=? AND USER_ID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, permission.getId());
            statement.setInt(1, user.getId());
            int count = statement.executeUpdate();
            logger.info(permission.getValue()+" Permissions deleted for user id: "+user.getId());
            return (count == 1);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteUserAllPermission(MyUser user) {
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

    public static UserPermissionRepository getInstance(){
        if(instance == null){
            instance = new UserPermissionRepository();
        }
        return instance;
    }

}
