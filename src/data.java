import java.sql.*;

public class data {
    static final String DB_URL = "jdbc:mysql://localhost:3306/farmer_database";
    static final String USER = "root";
    static final String PASS = "MOMDAD@20039m";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Execute a select statement to display the deleted record
            String sql = "SELECT * FROM info  WHERE Farmer_Id=59";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("Farmer Id: " + rs.getInt("Farmer_Id"));
                System.out.println("Farmer Name: " + rs.getString("Farmer_Name"));
                System.out.println("Crop: " + rs.getString("Crop"));
                System.out.println("Domicile: " + rs.getString("Domicile"));
            } else {
                System.out.println("No record found with Farmer Id 59");
            }


            // Execute a delete statement
            stmt = conn.createStatement();
            sql = "DELETE FROM info WHERE Farmer_Id=59";
            int rowsDeleted = stmt.executeUpdate(sql);
            System.out.println("Rows deleted: " + rowsDeleted);


            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // Finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                // nothing we can do
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
