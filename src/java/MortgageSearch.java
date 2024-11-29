import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MortgageSearch {
    private Connection conn;

    // Establish a connection to the database
    public MortgageSearch(Connection conn) {
        this.conn = conn;
    }

    // Search for mortgages based on the filters
    public void searchMortgages(FilterManager filterManager) {
        String query = "SELECT * FROM mortgages" + filterManager.getFilterQuery();

        // Attempt to query the database to search all mortgages given filter list
        try(Statement statement = conn.createStatement(); ResultSet result = statement.executeQuery(query);) {
            // Process all of the results
            while(result.next()) {

            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
