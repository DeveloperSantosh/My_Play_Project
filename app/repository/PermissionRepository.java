package repository;

import models.MyPermission;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PermissionRepository {
    private static final String TABLE_NAME = "MY_PERMISSIONS";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "PERMISSION_ID INTEGER AUTO_INCREMENT,"+
            "VALUE varchar(200) NOT NULL, "+
            "PRIMARY KEY (PERMISSION_ID))";
    Statement statement = null;
    private static PermissionRepository instance = null;

    private PermissionRepository() {
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

    public boolean save(@NotNull MyPermission permission) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (VALUE) VALUES ('"+ permission.getValue()+"');";
        int count = statement.executeUpdate(saveQuery);
        System.out.println(saveQuery);
        return (count == 1);
    }

    public List<MyPermission> findAllPermissions() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<MyPermission> permissions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
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
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "VALUE = '"+newPermission.getValue()+"',"+
                "WHERE PERMISSION_ID="+oldPermission.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean deletePermission(MyPermission permission) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE PERMISSION_ID="+permission.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static PermissionRepository getInstance(){
        if(instance == null){
            instance = new PermissionRepository();
        }
        return instance;
    }

}
