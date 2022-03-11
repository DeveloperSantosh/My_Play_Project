package repository;

import com.typesafe.config.ConfigFactory;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class MyDatabase {
    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            String DB_URL = ConfigFactory.load().getString("db.default.url");
            String USER = ConfigFactory.load().getString("db.default.username");
            String PASS = ConfigFactory.load().getString("db.default.password");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        if(instance == null) {
            instance = new MyDatabase();
        }
        return instance.connection;
    }
}
