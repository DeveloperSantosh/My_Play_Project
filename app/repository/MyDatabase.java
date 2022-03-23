package repository;

import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase{
    private Connection connection;

    public MyDatabase() {
        Logger logger = LoggerFactory.getLogger(MyDatabase.class);
        try {
            String DB_URL = "jdbc:mysql://localhost:3306/NewDB";
            String USER = "root";
            String PASS = "root";
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Database Connected");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public static Connection getConnection(){
        return new MyDatabase().connection;
    }
}
