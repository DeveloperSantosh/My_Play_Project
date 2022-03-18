package repository;

import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class MyDatabase {
    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        Logger logger = LoggerFactory.getLogger(MyDatabase.class);
        try {
            String DB_URL = ConfigFactory.load().getString("db.default.url");
            String USER = ConfigFactory.load().getString("db.default.username");
            String PASS = ConfigFactory.load().getString("db.default.password");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Database Connected");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public static Connection getConnection(){
        if(instance == null)
            instance = new MyDatabase();
        return instance.connection;
    }
}
