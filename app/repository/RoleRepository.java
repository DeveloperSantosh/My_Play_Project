package repository;

import models.MyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {
    private static RoleRepository instance;
    private final Connection connection;
    private final Logger logger;

    final String TABLE_NAME = "MY_ROLE";
    final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "ROLE_TYPE varchar(200), "+
            "ROLE_DESCRIPTION varchar(200) NOT NULL, "+
            "PRIMARY KEY (ROLE_TYPE))";

    private RoleRepository() {
        connection = MyDatabase.getConnection();
        logger = LoggerFactory.getLogger(RoleRepository.class);
        try {
            PreparedStatement statement = connection.prepareStatement(createTable);
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyRole role) {
        try {
            String saveQuery = "INSERT INTO "+TABLE_NAME+ " VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(saveQuery);
            statement.setString(1, role.getRoleType());
            statement.setString(2, role.getDescription());
            int count = statement.executeUpdate();
            logger.info(count+" Role saved successfully");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyRole findUserRoleByType(@NotEmpty @NotNull String roleType) {
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE=?";
            PreparedStatement statement = connection.prepareStatement(findQuery);
            statement.setString(1, roleType);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return MyRole.newBuilder()
                        .setRoleType(resultSet.getString("ROLE_TYPE"))
                        .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    public List<MyRole> findAllUserRoles() {
        List<MyRole> userRoles = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME;
            PreparedStatement statement = connection.prepareStatement(findQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                MyRole role = MyRole.newBuilder()
                        .setRoleType(resultSet.getString("ROLE_TYPE"))
                        .setDescription(resultSet.getString("ROLE_DESCRIPTION"))
                        .build();
                userRoles.add(role);
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return userRoles;
    }

    public boolean updateUserRoles(MyRole oldRole, MyRole newRole) {
        try {
            String query = "UPDATE "+ TABLE_NAME+ "SET ROLE_TYPE=?, ROLE_DESCRIPTION=? WHERE ROLE_TYPE=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newRole.getRoleType());
            statement.setString(2, newRole.getDescription());
            statement.setString(3, oldRole.getRoleType());
            int count = statement.executeUpdate();
            return (count == 1);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(MyRole role) {
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, role.getRoleType());
            int count = statement.executeUpdate();
            return (count == 1);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static RoleRepository getInstance(){
        if(instance == null){
            instance = new RoleRepository();
        }
        return instance;
    }
}
