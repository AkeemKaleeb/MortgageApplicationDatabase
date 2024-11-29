import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUpdater {
    private Connection conn;

    public DatabaseUpdater(Connection conn) {
        this.conn = conn;
    }

    public void updatePurchaserType(FilterManager fm) {
        FilterManager filterManager = fm;

        // Get Query
        String updateQuery = "UPDATE mortgages SET purchaser_type = 'private securization' WHERE " + filterManager.getFilterQuery();
    
        // Attempt to execute Query
        try {
            conn.setAutoCommit(false);                   // Disable auto-commit
            try(Statement statement = conn.createStatement()) {     // Execute the Query
                statement.executeUpdate(updateQuery);
            }

            conn.commit();                                          // Commit the transaction
        }
        catch(Exception e) {
            try {
                conn.rollback();                                    // Rollback any errors + Error Handling
            }
            catch(SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            try {
                conn.setAutoCommit(true);
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
