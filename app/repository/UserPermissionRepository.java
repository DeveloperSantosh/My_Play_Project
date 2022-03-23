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

    private final Logger logger = LoggerFactory.getLogger(UserPermissionRepository.class);
    private static UserPermissionRepository instance = null;
    private final String TABLE_NAME = "MY_USER_PERMISSIONS";

    private UserPermissionRepository() {
        createTable();
    }

    private void createTable(){
        String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "PERMISSION_ID INTEGER NOT NULL,"+
                "USER_ID INTEGER NOT NULL, "+
                "FOREIGN KEY (PERMISSION_ID) REFERENCES MY_PERMISSIONS (PERMISSION_ID), "+
                "FOREIGN KEY (USER_ID) REFERENCES MY_USER (USER_ID), "+
                "PRIMARY KEY (PERMISSION_ID, USER_ID))";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            statement.close();
            connection.close();
            logger.info("Table fetched successfully.");
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyUser user) {
        String saveQuery = "INSERT INTO "+ TABLE_NAME+ "VALUES(?,?)";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            for(MyPermission p: user.getPermissionList()){
                statement.setInt(1, user.getId());
                statement.setInt(2, p.getId());
                if(!PermissionRepository.getInstance().findAllPermissions().contains(p))
                    PermissionRepository.getInstance().save(p);
                statement.executeUpdate();
            }
            statement.close();
            connection.close();
            return true;
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyPermission> findAllPermissionsByUserId(int userId) {
        List<MyPermission> permissions = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int permissionId = resultSet.getInt("PERMISSION_ID");
                permissions.add(PermissionRepository.getInstance().findPermissionById(permissionId));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return permissions;
    }


    public boolean deleteUserPermission(MyUser user, MyPermission permission) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=? AND USER_ID=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, permission.getId());
            statement.setInt(1, user.getId());
            int count = statement.executeUpdate();
            logger.info(permission.getValue()+" Permissions deleted for user id: "+user.getId());
            statement.close();
            connection.close();
            return (count == 1);
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteUserAllPermission(MyUser user) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (SQLException | NullPointerException e) {
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
