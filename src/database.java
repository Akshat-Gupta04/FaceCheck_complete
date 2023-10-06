import java.sql.*;
import java.util.*;
public class database {
    public static void main(String[] args) {
        // set up database connection
        String url = "jdbc:mysql://localhost:3306/facecheck";
        String username = "root";
        String password = "MOMD ";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established");

            // take input from user
            Scanner input = new Scanner(System.in);
            System.out.println("Enter name");
            String value1 = input.nextLine();
            System.out.println("Enter roll no:");
            String value2 = input.nextLine();


            // execute SQL statement to insert values into table
            String sql = "INSERT INTO student (Name, Rollno) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, value1);
            stmt.setString(2, value2);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " rows inserted into table");

            stmt.close();
            input.close();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            // close database connection
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("Database connection closed");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

