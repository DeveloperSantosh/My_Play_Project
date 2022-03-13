package repository;

import models.UserRole;
import play.db.Database;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    String TABLE_NAME = "MY_ROLE";
    String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "ROLE_TYPE varchar(200), "+
            "ROLE_DESCRIPTION varchar(200) NOT NULL, "+
            "PRIMARY KEY (ROLE_TYPE))";
    Statement statement = null;

    private static RoleRepository instance;

    private RoleRepository() {
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

    public boolean save(@NotNull UserRole role) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (ROLE_TYPE, ROLE_DESCRIPTION) VALUES ('"+
                role.getRoleType()+"','"+
                role.getDescription()+"');";
        int count = statement.executeUpdate(saveQuery);
        System.out.println(saveQuery);
        return (count == 1);
    }

    public UserRole findUserRoleByType(String roleType) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE='"+roleType+"';";
        UserRole role = new UserRole();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            role.setRoleType(resultSet.getString("ROLE_TYPE"));
            role.setDescription(resultSet.getString("ROLE_DESCRIPTION"));
            return role;
        }
        return null;
    }

    public List<UserRole> findAllUserRoles() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<UserRole> userRoles = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            UserRole role = new UserRole();
            role.setRoleType(resultSet.getString("ROLE_TYPE"));
            role.setDescription(resultSet.getString("ROLE_DESCRIPTION"));
            userRoles.add(role);
        }
        return userRoles;
    }

    public boolean updateUserRoles(UserRole oldRole, UserRole newRole) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "ROLE_TYPE = '"+newRole.getRoleType()+"',"+
                "ROLE_DESCRIPTION = '"+newRole.getDescription()+"' "+
                "WHERE ROLE_TYPE= '"+oldRole.getRoleType()+"';";
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(UserRole role) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE ='"+role.getRoleType()+"';";
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    static RoleRepository getInstance(){
        if(instance == null){
            instance = new RoleRepository();
        }
        return instance;
    }
}
