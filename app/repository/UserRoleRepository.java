package repository;

import models.MyUser;
import models.MyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UserRoleRepository {

    private final Logger logger = LoggerFactory.getLogger(BlogRepository.class);
    private static UserRoleRepository instance = null;
    private final String TABLE_NAME = "MY_USER_ROLE";

    private UserRoleRepository() {}

    public boolean save(MyUser user) {
        String query = "INSERT INTO "+TABLE_NAME+" VALUES(?,?)";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                List<MyRole> savedRoles = RoleRepository.getInstance().findAllRoles();
                user.getRoleList().stream().filter(role-> !savedRoles.contains(role))
                        .forEach(RoleRepository.getInstance()::save);
                statement.setInt(2, user.getId());
                for (MyRole role: user.getRoleList()){
                    statement.setString(1, role.getRoleType());
                    statement.executeUpdate();
                }
                connection.commit();
                logger.info("User saved successfully");
                return true;
            } catch (SQLException e) {
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<MyUser> findUsersByRoleType(String roleType) {
        List<MyUser> users = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE ROLE_TYPE=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setString(1, roleType);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                users.add(UserRepository.getInstance().findUserByID(resultSet.getInt("USER_ID")));
            }
            resultSet.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return users;
    }

    public List<MyRole> findRolesByUserId(int userId) {
        List<MyRole> userRoles = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID = ?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                userRoles.add(RoleRepository.getInstance().findUserRoleByType(resultSet.getString("ROLE_TYPE")));
            }
            resultSet.close();
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return userRoles;
    }

    public boolean updateUserRole(MyUser oldUser, MyUser newUser) {
        return deleteUserAllRoles(oldUser) && save(newUser);
    }

    public boolean deleteUserRole(MyUser user, MyRole role) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE ROLE_TYPE =? AND USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, role.getRoleType());
                statement.setInt(2, user.getId());
                statement.executeUpdate();
                connection.commit();
                logger.info(role.getRoleType()+" role deleted successfully for "+user.getEmail());
                return true;
            } catch (SQLException e) {
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteUserAllRoles(MyUser user) {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, user.getId());
                statement.executeUpdate();
                connection.commit();
                logger.info("All roles deleted successfully for "+user.getEmail());
                return true;
            } catch (SQLException e) {
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public static UserRoleRepository getInstance(){
        if(instance == null){
            instance = new UserRoleRepository();
        }
        return instance;
    }

    private void createTable(){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "ROLE_TYPE varchar(200) NOT NULL, "+
                "USER_ID INTEGER NOT NULL, "+
                "FOREIGN KEY (ROLE_TYPE) REFERENCES MY_ROLE(ROLE_TYPE), "+
                "FOREIGN KEY (USER_ID) REFERENCES MY_USER(USER_ID), "+
                "PRIMARY KEY (ROLE_TYPE, USER_ID))";
        try (Connection connection = MyDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(createTableQuery)){
            statement.execute();
            logger.info("Table fetched successfully.");
        } catch (SQLException | NullPointerException e) {
            logger.warn(e.getMessage());
        }
    }

}
