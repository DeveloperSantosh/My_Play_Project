package repository;

import models.MyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static UserRepository instance = null;
    private static final String TABLE_NAME = "MY_USER";

    private UserRepository() {}

    public boolean save(@NotNull MyUser user) {
        if (!isValidUser(user)) return false;
        String insertQuery = "INSERT INTO "+TABLE_NAME+" (USERNAME, PASSWORD, EMAIL) VALUES ( ?, ?, ?)";
        try (Connection conn = MyDatabase.getConnection()){
            conn.setAutoCommit(false);
            Savepoint savepoint = conn.setSavepoint();
            boolean roleSaved = UserRoleRepository.getInstance().save(user);
            boolean permissionSaved = UserPermissionRepository.getInstance().save(user);
            try(PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                insertStatement.setString(1, user.getUsername());
                insertStatement.setString(2, user.getPassword());
                insertStatement.setString(3, user.getEmail());
                if (permissionSaved && roleSaved && insertStatement.executeUpdate()==1){
                    conn.commit();
                    logger.info("User saved successfully");
                    return  true;
                }
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }
            conn.rollback(savepoint);
            if (roleSaved) UserRoleRepository.getInstance().deleteUserAllRoles(user);
            if (permissionSaved)   UserPermissionRepository.getInstance().deleteUserAllPermission(user);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public MyUser findUserByID(@NotNull Integer id) {
        MyUser user = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                user =  MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return user;
    }

    public MyUser findUserByName(String name) {
        MyUser user = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE USERNAME=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                int id = resultSet.getInt("USER_ID");
                user = MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return user;
    }

    public MyUser findUserByEmail(String email) {
        MyUser user = null;
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE EMAIL = ?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(findQuery)){
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                int id = resultSet.getInt("USER_ID");
                user = MyUser.newBuilder()
                        .setId(id)
                        .setUsername(resultSet.getString("USERNAME"))
                        .setPassword(resultSet.getString("PASSWORD"))
                        .setEmail(resultSet.getString("EMAIL"))
                        .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
                        .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
                        .build();
            }
            resultSet.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            logger.warn(e.getMessage());
        }
        return user;
    }

    public List<MyUser> findAllUsers() {
        List<MyUser> users = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM "+ TABLE_NAME;
        try (Connection conn = MyDatabase.getConnection();
            PreparedStatement selectAllStatement = conn.prepareStatement(selectAllQuery);
            ResultSet resultSet = selectAllStatement.executeQuery()){
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
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return users;
    }

    public boolean updateUser(MyUser oldUser, MyUser newUser) {
        if (!(isValidUser(newUser) && isValidUser(oldUser))) return false;
        String updateQuery = "UPDATE "+TABLE_NAME+" SET USERNAME=?, PASSWORD=?, EMAIL=? WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            boolean permissionSaved;
            if (oldUser.getPermissionList().containsAll(newUser.getPermissionList()))
                permissionSaved = true;
            else permissionSaved = UserPermissionRepository.getInstance().save(newUser);
            boolean roleSaved;
            if (oldUser.getRoleList().containsAll(newUser.getRoleList()))
                roleSaved  = true;
            else roleSaved = UserRoleRepository.getInstance().save(newUser);
            try(PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newUser.getUsername());
                updateStatement.setString(2, newUser.getPassword());
                updateStatement.setString(3, newUser.getEmail());
                updateStatement.setInt(4, oldUser.getId());
                if (roleSaved && permissionSaved && updateStatement.executeUpdate()==1){
                    connection.commit();
                    logger.info(newUser.getEmail()+" User updated successfully");
                    return true;
                }
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }
            connection.rollback(savepoint);
            if (roleSaved) UserRoleRepository.getInstance().deleteUserAllRoles(newUser);
            if (permissionSaved) UserPermissionRepository.getInstance().deleteUserAllPermission(newUser);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean delete(MyUser user) {
        if (!isValidUser(user)) return false;
        String deleteQuery = "DELETE FROM "+ TABLE_NAME+ " WHERE USER_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            boolean roleDeleted = UserRoleRepository.getInstance().deleteUserAllRoles(user);
            boolean permissionDeleted = UserPermissionRepository.getInstance().deleteUserAllPermission(user);
            try(PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, user.getId());
                if (roleDeleted && permissionDeleted && deleteStatement.executeUpdate()==1) {
                    connection.commit();
                    logger.info(user.getEmail() + " USER DELETED SUCCESSFULLY");
                    return true;
                }
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }
            connection.rollback(savepoint);
            if (permissionDeleted)  UserPermissionRepository.getInstance().save(user);
            if (roleDeleted)    UserRoleRepository.getInstance().save(user);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean isValidUser(MyUser user){
        MyUser.Builder userBuilder = user.toBuilder();
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(regex);
        if(userBuilder.getEmail().isBlank())
            return false;
        else if(!pattern.matcher(user.getEmail()).matches())
            return false;
        else if(userBuilder.getPassword().isBlank())
            return false;
        else return !userBuilder.getUsername().isBlank();
    }

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    public void createTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "USER_ID INTEGER AUTO_INCREMENT, "+
                "USERNAME varchar(200) NOT NULL, "+
                "PASSWORD varchar(200) NOT NULL, "+
                "EMAIL varchar(200) UNIQUE, "+
                "PRIMARY KEY (USER_ID))";
        try(Connection connection = MyDatabase.getConnection();
            PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            if (statement.execute())
                logger.info("Table created successfully.");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

}
