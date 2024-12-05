import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUpdater {
    private Connection conn;

    public DatabaseUpdater(Connection conn) {
        this.conn = conn;
    }

    public boolean updatePurchaserType(String filterQuery) {
        // Update purchaser_type to 'private securitization' in the final_table
        String updateQuery = "UPDATE final_table SET purchaser_type = 'private securitization' " + filterQuery + ";";

        try {
            conn.commit(); // Commit any previous work
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false); // Start transaction

            try (Statement statement = conn.createStatement()) {
                int updatedRows = statement.executeUpdate(updateQuery);
                conn.commit();
                System.out.println(updatedRows + " rows updated to private securitization.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Update failed: " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
