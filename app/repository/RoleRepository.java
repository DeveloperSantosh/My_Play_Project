package repository;

import models.MyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    private final Logger logger = LoggerFactory.getLogger(RoleRepository.class);
    private static RoleRepository instance = null;
    private final String TABLE_NAME = "MY_ROLE";

    private RoleRepository() {
        createTable();
    }

    private void createTable(){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "ROLE_TYPE varchar(200), "+
                "ROLE_DESCRIPTION varchar(200) NOT NULL, "+
                "PRIMARY KEY (ROLE_TYPE))";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(createTableQuery);
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(MyRole role) {
        if (!isValidRole(role)) return false;
        String saveQuery = "INSERT INTO "+TABLE_NAME+ " VALUES(?,?)";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, role.getRoleType());
            statement.setString(2, role.getDescription());
            int count = statement.executeUpdate();
            logger.info(count+" Role saved successfully");
            statement.close();
            connection.close();
            return true;
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyRole findUserRoleByType(String roleType) {
        if (roleType == null || roleType.isBlank()) return null;
        MyRole role = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, roleType);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                role = MyRole.newBuilder()
                        .setRoleType(resultSet.getString("ROLE_TYPE"))
                        .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                        .build();
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return role;
    }

    public List<MyRole> findAllRoles() {
        List<MyRole> roles = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME;
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MyRole role = MyRole.newBuilder()
                        .setRoleType(resultSet.getString("ROLE_TYPE"))
                        .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                        .build();
                roles.add(role);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return roles;
    }

    public boolean updateUserRoles(MyRole oldRole, MyRole newRole) {
        if (!(isValidRole(newRole) && isValidRole(oldRole))) return false;
        String query = "UPDATE "+ TABLE_NAME+ "SET ROLE_TYPE=?, ROLE_DESCRIPTION=? WHERE ROLE_TYPE=?";
        try {
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newRole.getRoleType());
            statement.setString(2, newRole.getDescription());
            statement.setString(3, oldRole.getRoleType());
            int count = statement.executeUpdate();
            statement.close();
            connection.close();
            return (count == 1);
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(MyRole role) {
        if (!isValidRole(role)) return false;
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE =?";
            Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, role.getRoleType());
            int count = statement.executeUpdate();
            statement.close();
            connection.close();
            return (count == 1);
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    private boolean isValidRole(MyRole role){
        return role != null &&
                !role.getRoleType().isBlank() &&
                !role.getDescription().isBlank();
    }

    public static RoleRepository getInstance(){
        if(instance == null){
            instance = new RoleRepository();
        }
        return instance;
    }
}
