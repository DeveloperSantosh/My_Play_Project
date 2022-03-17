package repository;

import models.MyPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionRepository {
    private static PermissionRepository instance = null;
    private final Connection connection;
    private final Logger logger;

    private static final String TABLE_NAME = "MY_PERMISSIONS";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "PERMISSION_ID INTEGER AUTO_INCREMENT,"+
            "VALUE varchar(200) NOT NULL, "+
            "PRIMARY KEY (PERMISSION_ID))";

    private PermissionRepository() {
        logger = LoggerFactory.getLogger(PermissionRepository.class);
        connection = MyDatabase.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyPermission permission) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+ " VALUES (?,?);";
        PreparedStatement statement =connection.prepareStatement(saveQuery);
        statement.setString(2, permission.getValue());
        int count = statement.executeUpdate();
        logger.info("Permission saved successfully");
        return (count == 1);
    }

    public List<MyPermission> findAllPermissions() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        PreparedStatement statement = connection.prepareStatement(findQuery);

        List<MyPermission> permissions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            MyPermission permission = MyPermission.newBuilder()
                    .setId(resultSet.getInt("PERMISSION_ID"))
                    .setValue(resultSet.getString("VALUE"))
                    .build();
            permissions.add(permission);
        }
        return permissions;
    }

    public boolean updatePermission(MyPermission oldPermission, MyPermission newPermission) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET VALUE=? WHERE PERMISSION_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, newPermission.getValue());
        statement.setInt(2, oldPermission.getId());
        int count = statement.executeUpdate();
        return (count == 1);
    }

    public boolean deletePermission(MyPermission permission) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, permission.getId());
        int count = statement.executeUpdate();
        return (count == 1);
    }

    public static PermissionRepository getInstance(){
        if(instance == null){
            instance = new PermissionRepository();
        }
        return instance;
    }

}
