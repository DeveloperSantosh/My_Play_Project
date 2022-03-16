package repository;

import models.MyUser;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TABLE_NAME = "MY_USER";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "USER_ID INTEGER AUTO_INCREMENT, "+
            "USERNAME varchar(200) NOT NULL, "+
            "PASSWORD varchar(200) NOT NULL, "+
            "EMAIL varchar(200) UNIQUE, "+
            "PRIMARY KEY (USER_ID))";
    Statement statement = null;
    private static UserRepository instance = null;

    private UserRepository() {
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

    public boolean save(@NotNull MyUser user) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (USERNAME, PASSWORD, EMAIL) VALUES ('"+
                user.getUsername()+"','"+
                user.getPassword()+"','"+
                user.getEmail()+"');";
        int count = statement.executeUpdate(saveQuery);
        System.out.println(saveQuery);
        return (count == 1 && UserRoleRepository.getInstance().save(user)  && UserPermissionRepository.getInstance().save(user));
    }

    public MyUser findUserByID(Integer id) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID="+id;
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            return MyUser.newBuilder()
                    .setId(id)
                    .setUsername(resultSet.getString("USERNAME"))
                    .setPassword(resultSet.getString("PASSWORD"))
                    .setEmail(resultSet.getString("EMAIL"))
                    .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                    .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                    .build();
        }
        return null;
    }

    public MyUser findUserByName(String name) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USERNAME='"+name+"';";
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            int id = resultSet.getInt("USER_ID");
            return MyUser.newBuilder()
                    .setId(id)
                    .setUsername(resultSet.getString("USERNAME"))
                    .setPassword(resultSet.getString("PASSWORD"))
                    .setEmail(resultSet.getString("EMAIL"))
                    .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                    .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                    .build();
        }
        return null;
    }

    public MyUser findUserByEmail(String email) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE EMAIL = '"+email+"';";
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            int id = resultSet.getInt("USER_ID");
            return MyUser.newBuilder()
                    .setId(id)
                    .setUsername(resultSet.getString("USERNAME"))
                    .setPassword(resultSet.getString("PASSWORD"))
                    .setEmail(resultSet.getString("EMAIL"))
                    .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                    .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                    .build();
        }
        return null;
    }

    public List<MyUser> findAllUsers() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<MyUser> users = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            int id = resultSet.getInt("USER_ID");
            MyUser user = MyUser.newBuilder()
                    .setId(id)
                    .setUsername(resultSet.getString("USERNAME"))
                    .setPassword(resultSet.getString("PASSWORD"))
                    .setEmail(resultSet.getString("EMAIL"))
                    .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                    .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                    .build();
            users.add(user);
        }
        return users;
    }

    public boolean updateUser(MyUser oldUser, MyUser newUser) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "USERNAME = '"+newUser.getUsername()+"',"+
                "PASSWORD = '"+newUser.getPassword()+"',"+
                "EMAIL = '"+newUser.getEmail()+"' " +
                "WHERE USER_ID="+oldUser.getId();
        UserRoleRepository.getInstance().updateUserRole(oldUser, newUser);
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(MyUser user) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID="+user.getId();
        UserRoleRepository.getInstance().deleteUserAllRoles(user);
        UserPermissionRepository.getInstance().deleteUserAllPermission(user);
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
}
