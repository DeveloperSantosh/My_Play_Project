package repository;

import models.MyPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionRepository {

    private final Logger logger = LoggerFactory.getLogger(PermissionRepository.class);
    private static PermissionRepository instance = null;
    private static final String TABLE_NAME = "MY_PERMISSIONS";

    private PermissionRepository() {}

    public boolean save(MyPermission permission) {
        if (permission==null ) return false;
        String saveQuery = "INSERT INTO "+TABLE_NAME+ " (VALUE) VALUES (?);";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(saveQuery)) {
                statement.setString(1, permission.getValue());
                statement.executeUpdate();
                connection.commit();
                logger.info("Permission saved successfully");
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyPermission> findAllPermissions(){
        List<MyPermission> permissions = new ArrayList<>();
        String findQuery = "SELECT * FROM " + TABLE_NAME;
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                MyPermission permission = MyPermission.newBuilder()
                        .setId(resultSet.getInt("PERMISSION_ID"))
                        .setValue(resultSet.getString("VALUE"))
                        .build();
                permissions.add(permission);
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return permissions;
    }

    public MyPermission findPermissionById(Integer permissionId) {
        MyPermission permission = null;
        String findQuery = "SELECT * FROM " + TABLE_NAME + " WHERE PERMISSION_ID=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setInt(1, permissionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                permission = MyPermission.newBuilder()
                        .setId(resultSet.getInt("PERMISSION_ID"))
                        .setValue(resultSet.getString("VALUE"))
                        .build();
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return permission;
    }

    public MyPermission findPermissionByValue(String value) {
        MyPermission permission = null;
        String findQuery = "SELECT * FROM " + TABLE_NAME + " WHERE VALUE=?";
        try (Connection connection = MyDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setString(1, value);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                permission = MyPermission.newBuilder()
                        .setId(resultSet.getInt("PERMISSION_ID"))
                        .setValue(resultSet.getString("VALUE"))
                        .build();
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return permission;
    }

    public boolean updatePermission(MyPermission oldPermission, MyPermission newPermission) {
        String query = "UPDATE " + TABLE_NAME + " SET VALUE=? WHERE PERMISSION_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newPermission.getValue());
                statement.setInt(2, oldPermission.getId());
                statement.executeUpdate();
                connection.commit();
                logger.warn("Permission updated successfully");
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deletePermission(MyPermission permission) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, permission.getId());
                if (statement.executeUpdate() ==1) {
                    connection.commit();
                    return true;
                }
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static PermissionRepository getInstance(){
        if(instance == null){
            instance = new PermissionRepository();
        }
        return instance;
    }

    private void createTable(){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "PERMISSION_ID INTEGER AUTO_INCREMENT,"+
                "VALUE varchar(200) NOT NULL, "+
                "PRIMARY KEY (PERMISSION_ID))";
        try (Connection connection = MyDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(createTableQuery)){
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
    }

}
