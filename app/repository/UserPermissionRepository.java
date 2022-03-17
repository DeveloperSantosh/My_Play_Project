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

    public boolean save(@NotNull MyUser user) throws SQLException {
        int count = 0;
        String saveQuery = "INSERT INTO "+ TABLE_NAME+ "VALUES(?,?)";
        PreparedStatement statement = connection.prepareStatement(saveQuery);
        for(MyPermission p: user.getPermissionList()){
            statement.setInt(1, user.getId());
            statement.setInt(2, p.getId());
            if(!PermissionRepository.getInstance().findAllPermissions().contains(p))
                PermissionRepository.getInstance().save(p);
            count = statement.executeUpdate();
        }
        return (count == 1);
    }

    public List<MyPermission> findAllPermissionsByUserId(int userId) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
        PreparedStatement statement = connection.prepareStatement(findQuery);
        statement.setInt(1, userId);

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


    public boolean deleteUserPermission(MyUser user, MyPermission permission) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID=? AND USER_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, permission.getId());
        statement.setInt(1, user.getId());
        int count = statement.executeUpdate();
        logger.info("Permissions deleted for user id: "+user.getId());
        return (count == 1);
    }

    public boolean deleteUserAllPermission(MyUser user) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, user.getId());
        int count = statement.executeUpdate();
        return (count == 1);
    }

    public static UserPermissionRepository getInstance(){
        if(instance == null){
            instance = new UserPermissionRepository();
        }
        return instance;
    }

}
