package repository;

import models.MyBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageRepository {

    private static ImageRepository instance = null;
    private final Connection connection;
    private final Logger logger;

    private final String TABLE_NAME = "MY_IMAGES";
    private final String createTable = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
            "IMAGE_ID INTEGER AUTO_INCREMENT, "+
            "IMAGE_PATH varchar(200) NOT NULL, "+
            "BLOG_ID INTEGER NOT NULL, "+
            "PRIMARY KEY (IMAGE_ID),"+
            "FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID))";

    private ImageRepository() {
        logger = LoggerFactory.getLogger(ImageRepository.class);
        connection = MyDatabase.getConnection();
        try {
            Statement stm = connection.createStatement();
            stm.execute(createTable);
            stm.close();
            logger.info("Table fetched successfully");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean save(@NotNull MyBlog blog, @NotNull Integer savedBlogId) {
        String saveQuery = "INSERT INTO "+TABLE_NAME + " (IMAGE_PATH, BLOG_ID) VALUES (?,?)";
        try {
            PreparedStatement smt = connection.prepareStatement(saveQuery);
            int count = 0;
            for (String imagePath : blog.getImagePathList()) {
                smt.setString(1, imagePath);
                smt.setInt(2, savedBlogId);
                System.out.println(smt);
                count += smt.executeUpdate();
            }
            logger.info(count + " Image paths saved Successfully");
            return true;
        }catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<String> findImagesPathByBlogTitle(@NotNull @NotEmpty Integer blogId){
        List<String> imagePaths = new ArrayList<>();
        try {
            String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID=?";
            PreparedStatement smt = connection.prepareStatement(findQuery);
            smt.setInt(1, blogId);
            ResultSet resultSet = smt.executeQuery();
            while(resultSet.next()) {
                imagePaths.add(resultSet.getString("IMAGE_PATH"));
            }
            logger.info("Total images Obtained: "+imagePaths.size());
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return imagePaths;
    }

//    public List<String> findAllImagePaths() throws SQLException {
//        String findQuery = "SELECT * FROM "+ TABLE_NAME;
//        List<MyUser> users = new ArrayList<>();
//        ResultSet resultSet = statement.executeQuery(findQuery);
//        System.out.println(findQuery);
//        while(resultSet.next()) {
//            int id = resultSet.getInt("USER_ID");
//            MyUser user = MyUser.newBuilder()
//                    .setId(id)
//                    .setUsername(resultSet.getString("USERNAME"))
//                    .setPassword(resultSet.getString("PASSWORD"))
//                    .setEmail(resultSet.getString("EMAIL"))
//                    .addAllRole(UserRoleRepository.getInstance().findRolesByUserId(id))
//                    .addAllPermission(UserPermissionRepository.getInstance().findAllPermissionsByUserId(id))
//                    .build();
//            users.add(user);
//        }
//        return users;
//    }

    public boolean updateImagePath(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog){
        try {
            String query = "UPDATE "+ TABLE_NAME+ "SET IMAGE_PATH=? BLOG_TITLE=? WHERE BLOG_TITLE=?";
            PreparedStatement smt = connection.prepareStatement(query);
            int count = 0;
            for(String newPath:newBlog.getImagePathList()) {
                smt.setString(1, newPath);
                smt.setString(2, newBlog.getTitle());
                smt.setString(3, oldBlog.getTitle());
                count += smt.executeUpdate();
            }
            logger.info("Total update image path: "+count);
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteImagePath(@NotNull MyBlog blog, @NotEmpty @NotNull String imagePath){
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=? AND IMAGE_PATH=?";
            PreparedStatement smt = connection.prepareStatement(query);
            smt.setInt(1, blog.getId());
            smt.setString(2, imagePath);
            int count = smt.executeUpdate();
            logger.info("Deleted image path: "+imagePath);
            return (count == 1 && Files.deleteIfExists(Paths.get(imagePath)));
        } catch (SQLException | IOException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteAllImagePaths(@NotNull MyBlog blog){
        try {
            String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=? AND IMAGE_PATH=?";
            PreparedStatement smt = connection.prepareStatement(query);
            int count = 0;
            boolean result = true;
            for(String imagePath: blog.getImagePathList()){
                smt.setInt(1, blog.getId());
                smt.setString(2, imagePath);
                count += smt.executeUpdate();
                result = Files.deleteIfExists(Paths.get(imagePath));
            }
            logger.info("Total deleted image paths: "+count);
            return result;
        } catch (SQLException | IOException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }
    public static ImageRepository getInstance(){
        if(instance == null){
            instance = new ImageRepository();
        }
        return instance;
    }
}
