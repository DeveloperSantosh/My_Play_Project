package repository;

import models.MyBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    public boolean save(@NotNull MyBlog blog) throws SQLException {
        String saveQuery = "INSERT INTO "+TABLE_NAME + " (IMAGE_PATH, BLOG_ID) VALUES (?,?)";
        PreparedStatement smt = connection.prepareStatement(saveQuery);
        int count = 0;
        for(String imagePath: blog.getImagePathList()){
            smt.setString(1, imagePath);
            smt.setInt(2, blog.getId());
            count += smt.executeUpdate();
        }
        logger.info(count+" Image paths saved Successfully");
        return (count >= 1);
    }

    public List<String> findImagesPathByBlogTitle(@NotNull @NotEmpty Integer blogId) throws SQLException {
        List<String> imagePaths = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID=?";
        PreparedStatement smt = connection.prepareStatement(findQuery);
        smt.setInt(1, blogId);
        ResultSet resultSet = smt.executeQuery();
        while(resultSet.next()) {
            imagePaths.add(resultSet.getString("IMAGE_PATH"));
        }
        logger.info("Total images Obtained: "+imagePaths.size());
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

    public boolean updateImagePath(@NotNull MyBlog oldBlog, @NotNull MyBlog newBlog) throws SQLException {
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
        return (count >= 1);
    }

    public boolean deleteImagePath(@NotNull MyBlog blog, @NotEmpty @NotNull String imagePath) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_TITLE=? AND IMAGE_PATH=?";
        PreparedStatement smt = connection.prepareStatement(query);
        smt.setString(1, blog.getTitle());
        smt.setString(2, imagePath);
        int count = smt.executeUpdate();
        logger.info("Deleted image path: "+imagePath);
        return (count == 1);
    }

    public boolean deleteAllImagePaths(@NotNull MyBlog blog) throws SQLException {
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_TITLE=?";
        PreparedStatement smt = connection.prepareStatement(query);
        int count = 0;
        for(String imagePath: blog.getImagePathList()){
            smt.setString(1, blog.getTitle());
            smt.setString(2, imagePath);
            count += smt.executeUpdate();
        }
        logger.info("Total deleted image paths: "+count);
        return true;
    }
    public static ImageRepository getInstance(){
        if(instance == null){
            instance = new ImageRepository();
        }
        return instance;
    }
}
