package task_management_system;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";
    private static final String PASS = "";          

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
