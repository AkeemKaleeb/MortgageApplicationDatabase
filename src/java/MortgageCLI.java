import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * Ensure  postgresql JDBC jar is a part of class path or pom.xml
 * pg_ctl restart -D "C:\Program Files\PostgreSQL\17\data"
 */

public class MortgageCLI {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mortgageapplication";
        String user = "postgres";
        String password = "admin";

        // auto close connection
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
                // Run Code
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }


        // Initialize program
        Scanner scanner = new Scanner(System.in);
        FilterManager filterManager = new FilterManager();
        boolean running = true;

        System.out.println("Welcome to the Mortgage Application.\n");
        while(running) {
            System.out.println("");
            filterManager.displayActiveFilters();
            System.out.println("");
            
            // Prompt User
            System.out.println(
                "Please choose an option:\n"
                + "1. Add Filter\n"
                + "2. Delete Filter\n"
                + "3. Calculate Rate\n"
                + "4. Exit"
            );

            // Collect Input and Execute Instruction
            int option = scanner.nextInt();
            System.out.println("");
            switch(option) {
                case 1:     // Add a Filter to the List
                    System.out.println("Add Filter");
                    System.out.println(
                        "1. MSAMD\n"
                        + "2. Income to Debt Ratio\n"
                        + "3. County\n"
                        + "4. Loan Type\n"
                        + "5. Tract to MSAMD Income\n"
                        + "6. Loan Purpose\n"
                        + "7. Property Type\n"
                        + "8. Owner Occupied"
                    );
                    
                    int filterOption = scanner.nextInt();
                    filterManager.addFilter(filterOption);

                    break;
                case 2:     // Remove a Filter from the List 
                    System.out.println("Delete Filter");
                    break;
                case 3:     // Calculate the Rate of the Mortgage
                    System.out.println("Calculate Rate");
                    break;
                case 4:     // Exit the Program
                    System.out.println("Exiting...");
                    running = false;
                    break;
                default:    // Invalid Option/Error Handling
                    System.out.println("Invalid option");
            }
        }
    }
}
