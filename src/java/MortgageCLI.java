import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MortgageCLI {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mortgageapplication";
        String user = "postgres";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
                conn.setAutoCommit(true); // default

                Scanner scanner = new Scanner(System.in);
                FilterManager filterManager = new FilterManager();
                boolean running = true;

                DatabaseUpdater dbUpdater = new DatabaseUpdater(conn);
                RateCalculator rateCalculator = new RateCalculator(conn);
                MortgageCreator mortgageCreator = new MortgageCreator(conn);

                System.out.println("Welcome to the Mortgage Application.\n");

                while (running) {
                    System.out.println("");
                    filterManager.displayActiveFilters();
                    printCountAndSum(conn, filterManager.getFilterQuery());
                    System.out.println("");

                    System.out.println(
                            "Please choose an option:\n"
                                    + "1. Add Filter\n"
                                    + "2. Delete Filter\n"
                                    + "3. Calculate Rate\n"
                                    + "4. Print Filtered Results\n"
                                    + "5. Add New Mortgage\n"
                                    + "6. Exit"
                    );

                    int option;
                    try {
                        option = Integer.parseInt(scanner.next());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    switch (option) {
                        case 1: // Add a Filter
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
                        case 2: // Remove a Filter
                            System.out.println("Delete Filter");
                            System.out.println(
                                    "1. MSAMD\n"
                                            + "2. Income to Debt Ratio\n"
                                            + "3. County\n"
                                            + "4. Loan Type\n"
                                            + "5. Tract to MSAMD Income\n"
                                            + "6. Loan Purpose\n"
                                            + "7. Property Type\n"
                                            + "8. Owner Occupied\n"
                                            + "0. Clear All"
                            );

                            int deleteOption = scanner.nextInt();
                            if (deleteOption == 0) {
                                filterManager.clearFilters();
                            } else {
                                filterManager.removeFilterByType(deleteOption);
                            }
                            break;
                        case 3: // Calculate the Rate
                            System.out.println("Calculate Rate...");
                            RateCalculator.RateResult rr = rateCalculator.calculateRate(filterManager.getFilterQuery());
                            if (rr.empty) {
                                System.out.println("No mortgages found with current filters.");
                                break;
                            }
                            System.out.println("Total Cost of Securitization: " + rr.totalLoanAmount);
                            System.out.println("Weighted Average Rate: " + rr.rate + "%");
                            System.out.println("Do you accept this rate and cost? (y/n)");
                            String ans = scanner.next();
                            if (ans.equalsIgnoreCase("y")) {
                                boolean success = dbUpdater.updatePurchaserType(filterManager.getFilterQuery());
                                if (success) {
                                    System.out.println("Mortgages successfully updated to private securitization. Exiting...");
                                    running = false;
                                } else {
                                    System.out.println("Failed to update mortgages. Returning to main menu.");
                                }
                            } else {
                                System.out.println("Rejected. Returning to main menu.");
                            }
                            break;
                        case 4: // Print Filtered Results
                            printFilteredResults(conn, filterManager.getFilterQuery());
                            break;
                        case 5: // Add new mortgage
                            mortgageCreator.addNewMortgage();
                            break;
                        case 6: // Exit
                            System.out.println("Exiting...");
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid option");
                    }
                }

                scanner.close();
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printFilteredResults(Connection conn, String filterQuery) {
        // final_table is the main table
        String sql = "SELECT application_id, msamd, loan_type, loan_purpose, property_type, owner_occupancy, loan_amount_000s FROM final_table " + filterQuery;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.println(
                        "AppID: " + rs.getInt("application_id") +
                                ", MSAMD: " + rs.getString("msamd") +
                                ", Loan Type: " + rs.getString("loan_type") +
                                ", Loan Purpose: " + rs.getString("loan_purpose") +
                                ", Property Type: " + rs.getString("property_type") +
                                ", Owner Occupied: " + rs.getString("owner_occupancy") +
                                ", Loan Amount: " + rs.getString("loan_amount_000s") + "000"
                );
            }
            if (!any) {
                System.out.println("No results found.");
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printCountAndSum(Connection conn, String filterQuery) {
        String sql = "SELECT COUNT(*) AS cnt, SUM(loan_amount_000s::numeric) AS total_loan_amount FROM final_table " + filterQuery;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                long count = rs.getLong("cnt");
                double sum = rs.getDouble("total_loan_amount") * 1000; // convert thousands to actual
                System.out.println("Number of matched rows: " + count);
                System.out.println("Sum of matched loan amount: " + sum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
