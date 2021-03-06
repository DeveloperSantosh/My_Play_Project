package repository;

import models.User;
import models.UserPermission;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserPermissionRepository {
    private static final String TABLE_NAME = "MY_USER_PERMISSIONS";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "PERMISSION_ID INTEGER NOT NULL,"+
            "USER_ID INTEGER NOT NULL, "+
            "PRIMARY KEY (PERMISSION_ID, USER_ID))";
    Statement statement = null;
    private static UserPermissionRepository instance = null;

    private UserPermissionRepository() {
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
        int count = 0;
        for(UserPermission p: user.getPermissions()){
            String saveQuery = "INSERT INTO "+TABLE_NAME+
                    " (PERMISSION_ID, USER_ID) VALUES ("+ user.getId()+","+p.getId()+");";
            boolean save = PermissionRepository.getInstance().save(p);
            count = statement.executeUpdate(saveQuery);
            System.out.println(saveQuery);
        }
        return (count >= 1);
    }

    public List<UserPermission> findAllPermissionsByUserId(int userId) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID="+userId;
        List<UserPermission> permissions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            UserPermission permission = new UserPermission();
            permission.setId(resultSet.getInt("PERMISSION_ID"));
            permission.setValue(resultSet.getString("VALUE"));
            permissions.add(permission);
        }
        return permissions;
    }


    public boolean deleteUserPermission(User user, UserPermission permission) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID="+permission.getId()+" AND USER_ID="+user.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean deleteUserAllPermission(User user) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID="+user.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static UserPermissionRepository getInstance(){
        if(instance == null){
            instance = new UserPermissionRepository();
        }
        return instance;
    }

}
