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

    private UserPermissionRepository() {}

    public boolean save(@NotNull MyUser user) {
        String saveQuery = "INSERT INTO "+ TABLE_NAME+ "VALUES(?,?)";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(saveQuery)) {
                List<MyPermission> savedPermissions = PermissionRepository.getInstance().findAllPermissions();
                user.getPermissionList().stream()
                        .filter(newPermission -> !savedPermissions.contains(newPermission))
                        .forEach(PermissionRepository.getInstance()::save);
                for (MyPermission p: user.getPermissionList()){
                    int permissionId = PermissionRepository.getInstance().findPermissionByValue(p.getValue()).getId();
                    statement.setInt(1, permissionId);
                    statement.setInt(2, user.getId());
                    statement.executeUpdate();
                }
                connection.commit();
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean save(MyPermission permission, MyUser user) {
        String saveQuery = "INSERT INTO " + TABLE_NAME + " (PERMISSION_ID, USER_ID) VALUES(?,?)";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(saveQuery)) {
            statement.setInt(1, permission.getId());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyPermission> findAllPermissionsByUserId(int userId) {
        List<MyPermission> permissions = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int permissionId = resultSet.getInt("PERMISSION_ID");
                permissions.add(PermissionRepository.getInstance().findPermissionById(permissionId));
            }
            resultSet.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return permissions;
    }

    public boolean deleteUserPermission(MyUser user, MyPermission permission) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=? AND USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, permission.getId());
                statement.setInt(1, user.getId());
                statement.executeUpdate();
                connection.commit();
                logger.info(permission.getValue() + " Permissions deleted for user id: " + user.getId());
                return true;
            } catch (SQLException e) {
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteUserAllPermission(MyUser user) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, user.getId());
                statement.executeUpdate();
                connection.commit();
                logger.info("All Permissions deleted for user id: " + user.getId());
                return true;
            } catch (SQLException e) {
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
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

    private void createTable(){
        String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "PERMISSION_ID INTEGER NOT NULL,"+
                "USER_ID INTEGER NOT NULL, "+
                "FOREIGN KEY (PERMISSION_ID) REFERENCES MY_PERMISSIONS (PERMISSION_ID), "+
                "FOREIGN KEY (USER_ID) REFERENCES MY_USER (USER_ID), "+
                "PRIMARY KEY (PERMISSION_ID, USER_ID))";
        try (Connection connection = MyDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(createTable)){
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
    }

}
