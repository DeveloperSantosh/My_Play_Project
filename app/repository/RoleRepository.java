package repository;


import models.MyRole;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    final String TABLE_NAME = "MY_ROLE";
    final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "ROLE_TYPE varchar(200), "+
            "ROLE_DESCRIPTION varchar(200) NOT NULL, "+
            "PRIMARY KEY (ROLE_TYPE))";

    private static RoleRepository instance;
    Statement statement = null;

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

    public boolean save(@NotNull MyRole role) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME+
                " (ROLE_TYPE, ROLE_DESCRIPTION) VALUES ('"+
                role.getRoleType()+"','"+
                role.getDescription()+"');";
        int count = statement.executeUpdate(saveQuery);
        System.out.println(saveQuery);
        return (count == 1);
    }

    public MyRole findUserRoleByType(String roleType) throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE='"+roleType+"';";
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        if(resultSet.next()) {
            return MyRole.newBuilder()
                    .setRoleType(resultSet.getString("ROLE_TYPE"))
                    .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                    .build();
        }
        return null;
    }

    public List<MyRole> findAllUserRoles() throws SQLException {
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        List<MyRole> userRoles = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(findQuery);
        System.out.println(findQuery);
        while(resultSet.next()) {
            MyRole role = MyRole.newBuilder()
                    .setRoleType(resultSet.getString("ROLE_TYPE"))
                    .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                    .build();
            userRoles.add(role);
        }
        return userRoles;
    }

    public boolean updateUserRoles(MyRole oldRole, MyRole newRole) throws SQLException {
        String query = "UPDATE "+TABLE_NAME+" SET "+
                "ROLE_TYPE = '"+newRole.getRoleType()+"',"+
                "ROLE_DESCRIPTION = '"+newRole.getDescription()+"' "+
                "WHERE ROLE_TYPE= '"+oldRole.getRoleType()+"';";
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public boolean delete(MyRole role) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE ='"+role.getRoleType()+"';";
        int count = statement.executeUpdate(query);
        System.out.println(query);
        return (count == 1);
    }

    public static RoleRepository getInstance(){
        if(instance == null){
            instance = new RoleRepository();
        }
        return instance;
    }
}
