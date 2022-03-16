package repository;

import models.MyUser;
import models.MyRole;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRoleRepository {
    String TABLE_NAME = "MY_USER_ROLE";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "ROLE_TYPE varchar(200) NOT NULL, "+
            "USER_ID INTEGER NOT NULL, "+
            "PRIMARY KEY (ROLE_TYPE, USER_ID))";
    Statement statement = null;
    private static UserRoleRepository instance = null;

    private UserRoleRepository() {
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
        int count = 0;
        for(MyRole role: user.getRoleList()) {
            String saveQuery = "INSERT INTO " + TABLE_NAME +
                    " (ROLE_TYPE, USER_ID) VALUES ('" +
                    role.getRoleType() + "','" +
                    user.getId() + "');";
            if(!RoleRepository.getInstance().findAllUserRoles().contains(role))
                RoleRepository.getInstance().save(role);
            count = statement.executeUpdate(saveQuery);
            System.out.println(saveQuery);
        }
        return (count >= 1);
    }

    public List<MyUser> findUsersByRoleType(String roleType) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE='"+roleType+"';";
        List<MyUser> users = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            users.add(UserRepository.getInstance().findUserByID(resultSet.getInt("USER_ID")));
        }
        return users;
    }

    public List<MyRole> findRolesByUserId(int userId) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID = "+userId;
        List<MyRole> userRoles = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            userRoles.add(RoleRepository.getInstance().
                    findUserRoleByType(resultSet.getString("ROLE_TYPE")));
        }
        return userRoles;
    }

    public boolean updateUserRole(MyUser oldUser, MyUser newUser) throws SQLException {
        int count =0;
        for(MyRole role:newUser.getRoleList()) {
            String query = "UPDATE " + TABLE_NAME + " SET ROLE_TYPE ='" + role.getRoleType() + "', USER_ID="+newUser.getId()+
                    " WHERE USER_ID=" + oldUser.getId()+" and ROLE_TYPE='"+role.getRoleType()+"'";
            count = statement.executeUpdate(query);
            System.out.println(query);
        }
        return (count == 1);

    }

    public boolean deleteUserRole(MyUser user, MyRole role) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE ='"+role.getRoleType()+"' and USER_ID="+user.getId();
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean deleteUserAllRoles(MyUser user) throws SQLException {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID="+user.getId();
            int count = statement.executeUpdate(query);
            System.out.println(query);

        return (count >= 0);
    }
    public static UserRoleRepository getInstance(){
        if(instance == null){
            instance = new UserRoleRepository();
        }
        return instance;
    }
}
