import java.sql.*;

public class DbHelper {
    private String dbUrl = "jdbc:mysql://localhost:3306/bolutagdb";
    private String user = "root"; 
    private String pass = "12345";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, user, pass);
    }
}