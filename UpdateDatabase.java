import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class UpdateDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/childcare_db";
        String user = "root";
        String password = "root1234";

        String[] queries = {
            "ALTER TABLE payment ADD COLUMN description VARCHAR(255) NULL"
        };

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            for (String query : queries) {
                try {
                    stmt.execute(query);
                    System.out.println("Executed: " + query);
                } catch (Exception e) {
                    System.out.println("Skipped (probably exists): " + query);
                }
            }
            System.out.println("Database patch complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
