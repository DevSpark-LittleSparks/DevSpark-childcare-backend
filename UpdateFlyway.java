import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateFlyway {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/childcare_db";
        String user = "root";
        String password = "root1234";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Rename local migrations in flyway history to match the new version names
            String sql1 = "UPDATE flyway_schema_history SET version = '39' WHERE version = '29' AND description = 'add name with initials to child'";
            String sql2 = "UPDATE flyway_schema_history SET version = '40' WHERE version = '30' AND description = 'add billing paid to parent'";
            String sql3 = "UPDATE flyway_schema_history SET version = '41' WHERE version = '31' AND description = 'update admin and director request'";

            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                int rows = pstmt1.executeUpdate();
                System.out.println("Updated V29 -> V39: " + rows + " rows.");
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                int rows = pstmt2.executeUpdate();
                System.out.println("Updated V30 -> V40: " + rows + " rows.");
            }
            try (PreparedStatement pstmt3 = conn.prepareStatement(sql3)) {
                int rows = pstmt3.executeUpdate();
                System.out.println("Updated V31 -> V41: " + rows + " rows.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
