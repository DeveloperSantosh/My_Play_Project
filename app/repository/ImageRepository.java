package repository;

import models.MyBlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageRepository {

    private final Logger logger = LoggerFactory.getLogger(ImageRepository.class);
    private static ImageRepository instance = null;
    private final String TABLE_NAME = "MY_IMAGES";

    private ImageRepository() {}

    public boolean save(MyBlog blog, Integer savedBlogId) {
        String saveQuery = "INSERT INTO "+TABLE_NAME+ " (IMAGE_PATH, BLOG_ID) VALUES (?,?)";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement smt = connection.prepareStatement(saveQuery)) {
                int count = 0;
                for (String imagePath : blog.getImagePathList()) {
                    smt.setString(1, imagePath);
                    smt.setInt(2, savedBlogId);
                    count += smt.executeUpdate();
                }
                logger.info(count + " Image paths saved Successfully");
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        }catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public List<String> findImagesPathByBlogId(Integer blogId){
        List<String> imagePaths = new ArrayList<>();
        String findQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE BLOG_ID=?";
        try (Connection connection = MyDatabase.getConnection();
            PreparedStatement smt = connection.prepareStatement(findQuery)){
            smt.setInt(1, blogId);
            ResultSet resultSet = smt.executeQuery();
            while(resultSet.next()) {
                imagePaths.add(resultSet.getString("IMAGE_PATH"));
            }
            resultSet.close();
            logger.info("Total images Obtained: "+imagePaths.size());
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return imagePaths;
    }

    public boolean updateImagePath(MyBlog oldBlog, MyBlog newBlog){
        String deleteQuery = "DELETE FROM "+TABLE_NAME+" WHERE BLOG_ID =?";
        String saveQuery = "INSERT INTO "+TABLE_NAME+ " (IMAGE_PATH, BLOG_ID) VALUES (?,?)";
        try (Connection connection = MyDatabase.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try (PreparedStatement delete = connection.prepareStatement(deleteQuery);
                 PreparedStatement insert = connection.prepareStatement(saveQuery)) {
                delete.setInt(1, oldBlog.getId());
                delete.executeUpdate();
                insert.setInt(2, newBlog.getId());
                for (String imagePath : newBlog.getImagePathList()) {
                    insert.setString(1, imagePath);
                    insert.executeUpdate();

                }
                List<String> imagesPaths = oldBlog.getImagePathList().stream()
                        .filter(imagePath-> !newBlog.getImagePathList().contains(imagePath))
                                .collect(Collectors.toList());
                deleteImageFiles(imagesPaths);
                connection.commit();
                return true;
            } catch (SQLException e) {
                logger.warn(e.getMessage());
                connection.rollback(savepoint);
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteImagePath(MyBlog blog, String imagePath){
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=? AND IMAGE_PATH=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement smt = connection.prepareStatement(query)) {
                smt.setInt(1, blog.getId());
                smt.setString(2, imagePath);
                smt.executeUpdate();
                logger.info("Deleted image path: " + imagePath);
                connection.commit();
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteAllImagePaths(MyBlog blog){
        String query = "DELETE FROM "+ TABLE_NAME+ " WHERE BLOG_ID=?";
        try (Connection connection = MyDatabase.getConnection()){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try (PreparedStatement smt = connection.prepareStatement(query)) {
                smt.setInt(1, blog.getId());
                int count = smt.executeUpdate();
                connection.commit();
                logger.info("Total deleted image paths: " + count);
                return true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.warn(e.getMessage());
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteAllImages(MyBlog blog){
        try {
            return Files.deleteIfExists(Path.of("assets/images/'" + blog.getTitle() + "'"));
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public boolean deleteImageFiles(List<String> imagePaths){
        try {
            for (String imagePath: imagePaths){
                if(!Files.deleteIfExists(Paths.get(imagePath)))
                    return false;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return true;
    }

    public static ImageRepository getInstance(){
        if(instance == null){
            instance = new ImageRepository();
        }
        return instance;
    }

    private void createTable(){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                "IMAGE_ID INTEGER AUTO_INCREMENT, "+
                "IMAGE_PATH varchar(200) NOT NULL, "+
                "BLOG_ID INTEGER NOT NULL, "+
                "PRIMARY KEY (IMAGE_ID),"+
                "FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID))";
        try (Connection connection = MyDatabase.getConnection();
             Statement stm = connection.createStatement()){
            stm.execute(createTableQuery);
            logger.info("Table fetched successfully");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

}
