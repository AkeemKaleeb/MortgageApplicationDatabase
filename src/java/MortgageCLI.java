
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MortgageCLI {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mortgageapplication";
        String user = "postgres";
        String password = "admin";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
                conn.setAutoCommit(true);

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
                        case 1:
                            addFilterMenu(conn, filterManager, scanner);
                            break;
                        case 2:
                            deleteFilterMenu(filterManager, scanner);
                            break;
                        case 3:
                            calculateRateMenu(conn, filterManager, dbUpdater, rateCalculator, scanner);
                            break;
                        case 4:
                            printFilteredResults(conn, filterManager.getFilterQuery());
                            break;
                        case 5:
                            mortgageCreator.addNewMortgage();
                            break;
                        case 6:
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

    private static void addFilterMenu(Connection conn, FilterManager filterManager, Scanner scanner) {
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
        scanner.nextLine(); // consume leftover newline

        switch (filterOption) {
            case 1: // MSAMD
                List<MSAMDOption> msamdOptions = getMSAMDOptions(conn);
                System.out.println("Available MSAMD Options:");
                for (int i=0; i<msamdOptions.size(); i++) {
                    System.out.println((i+1) + ". " + msamdOptions.get(i).displayName);
                }
                System.out.println("Enter the numbers of the MSAMD options you want to filter by (comma separated):");
                String msamdSelection = scanner.nextLine().trim();
                if (!msamdSelection.isEmpty()) {
                    List<Integer> chosen = Arrays.stream(msamdSelection.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    List<String> selectedNames = new ArrayList<>();
                    boolean byName = true;
                    for (int c : chosen) {
                        MSAMDOption opt = msamdOptions.get(c-1);
                        selectedNames.add(opt.value);
                        if (!opt.byName) byName = false; // if any uses code
                    }
                    filterManager.addMSAMDFilter(selectedNames, byName);
                }
                break;
            case 2: // Income to Debt Ratio
                System.out.println("Enter minimum income to debt ratio (or blank): ");
                String minStr = scanner.nextLine().trim();
                Double minRatio = minStr.isEmpty() ? null : Double.parseDouble(minStr);

                System.out.println("Enter maximum income to debt ratio (or blank): ");
                String maxStr = scanner.nextLine().trim();
                Double maxRatio = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);

                filterManager.addIncomeToDebtRatioFilter(minRatio, maxRatio);
                break;
            case 3: // County
                List<String> counties = getDistinctValues(conn, "county_name");
                if (counties.isEmpty()) {
                    System.out.println("No counties found.");
                    break;
                }
                System.out.println("Available Counties:");
                for (int i=0; i<counties.size(); i++) {
                    System.out.println((i+1) + ". " + counties.get(i));
                }
                System.out.println("Select counties by number (comma separated): ");
                String countySelect = scanner.nextLine().trim();
                if (!countySelect.isEmpty()) {
                    List<String> chosen = parseSelection(countySelect, counties);
                    filterManager.addCountyFilter(chosen);
                }
                break;
            case 4: // Loan Type
                List<String> loanTypes = getDistinctValues(conn, "loan_type_name");
                System.out.println("Available Loan Types:");
                printListWithNumbers(loanTypes);
                System.out.println("Select loan types by number (comma separated): ");
                String ltSelect = scanner.nextLine().trim();
                if (!ltSelect.isEmpty()) {
                    List<String> chosen = parseSelection(ltSelect, loanTypes);
                    filterManager.addLoanTypeFilter(chosen);
                }
                break;
            case 5: // Tract to MSAMD Income
                System.out.println("Enter minimum tract_to_msamd_income (or blank): ");
                String tMinStr = scanner.nextLine().trim();
                Double tMin = tMinStr.isEmpty() ? null : Double.parseDouble(tMinStr);

                System.out.println("Enter maximum tract_to_msamd_income (or blank): ");
                String tMaxStr = scanner.nextLine().trim();
                Double tMax = tMaxStr.isEmpty() ? null : Double.parseDouble(tMaxStr);

                filterManager.addTractToMSAMDIncomeFilter(tMin, tMax);
                break;
            case 6: // Loan Purpose
                List<String> lpList = getDistinctValues(conn, "loan_purpose_name");
                System.out.println("Available Loan Purposes:");
                printListWithNumbers(lpList);
                System.out.println("Select loan purposes by number (comma separated): ");
                String lpSel = scanner.nextLine().trim();
                if (!lpSel.isEmpty()) {
                    List<String> chosen = parseSelection(lpSel, lpList);
                    filterManager.addLoanPurposeFilter(chosen);
                }
                break;
            case 7: // Property Type
                List<String> ptList = getDistinctValues(conn, "property_type_name");
                System.out.println("Available Property Types:");
                printListWithNumbers(ptList);
                System.out.println("Select property types by number (comma separated): ");
                String ptSel = scanner.nextLine().trim();
                if (!ptSel.isEmpty()) {
                    List<String> chosen = parseSelection(ptSel, ptList);
                    filterManager.addPropertyTypeFilter(chosen);
                }
                break;
            case 8: // Owner Occupied
                List<String> ooList = getDistinctValues(conn, "owner_occupancy_name");
                System.out.println("Available Owner Occupancy Options:");
                printListWithNumbers(ooList);
                System.out.println("Select owner occupancy by number (comma separated): ");
                String ooSel = scanner.nextLine().trim();
                if (!ooSel.isEmpty()) {
                    List<String> chosen = parseSelection(ooSel, ooList);
                    filterManager.addOwnerOccupancyFilter(chosen);
                }
                break;
            default:
                System.out.println("Invalid filter option");
        }
    }

    private static void deleteFilterMenu(FilterManager filterManager, Scanner scanner) {
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
    }

    private static void calculateRateMenu(Connection conn, FilterManager filterManager, DatabaseUpdater dbUpdater, RateCalculator rateCalculator, Scanner scanner) {
        System.out.println("Calculate Rate...");
        RateCalculator.RateResult rr = rateCalculator.calculateRate(filterManager.getFilterQuery());
        if (rr.empty) {
            System.out.println("No mortgages found with current filters.");
            return;
        }
        System.out.println("Total Cost of Securitization: " + rr.totalLoanAmount);
        System.out.println("Weighted Average Rate: " + rr.rate + "%");
        System.out.println("Do you accept this rate and cost? (y/n)");
        String ans = scanner.next();
        if (ans.equalsIgnoreCase("y")) {
            boolean success = dbUpdater.updatePurchaserType(filterManager.getFilterQuery());
            if (success) {
                System.out.println("Mortgages successfully updated to private securitization. Exiting...");
                System.exit(0);
            } else {
                System.out.println("Failed to update mortgages. Returning to main menu.");
            }
        } else {
            System.out.println("Rejected. Returning to main menu.");
        }
    }

    private static void printFilteredResults(Connection conn, String filterQuery) {
        String sql = "SELECT application_id, msamd, msamd_name, loan_type_name, loan_purpose_name, property_type_name, owner_occupancy_name, loan_amount_000s, county_name " +
                 "FROM final_table " + filterQuery;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                String msamdVal = rs.getString("msamd_name");
                if (msamdVal == null || msamdVal.trim().isEmpty()) {
                    msamdVal = rs.getString("msamd");
                }
                System.out.println(
                        "AppID: " + rs.getInt("application_id") +
                                ", MSAMD: " + msamdVal +
                                ", Loan Type: " + rs.getString("loan_type_name") +
                                ", Loan Purpose: " + rs.getString("loan_purpose_name") +
                                ", Property Type: " + rs.getString("property_type_name") +
                                ", Owner Occupied: " + rs.getString("owner_occupancy_name") +
                                ", Loan Amount: " + rs.getString("loan_amount_000s") + "000" +
                                ", County: " + rs.getString("county_name")
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
                double sum = rs.getDouble("total_loan_amount") * 1000; 
                System.out.println("Number of matched rows: " + count);
                System.out.println("Sum of matched loan amount: " + sum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getDistinctValues(Connection conn, String column) {
        String sql = "SELECT DISTINCT " + column + " FROM final_table WHERE action_taken='1' AND " + column + " IS NOT NULL AND " + column + " <> '' ORDER BY " + column;
        List<String> vals = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vals.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vals;
    }

    private static void printListWithNumbers(List<String> list) {
        for (int i=0; i<list.size(); i++) {
            System.out.println((i+1) + ". " + list.get(i));
        }
    }

    private static List<String> parseSelection(String input, List<String> fullList) {
        List<Integer> indexes = Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<String> chosen = new ArrayList<>();
        for (int idx : indexes) {
            chosen.add(fullList.get(idx-1));
        }
        return chosen;
    }

    private static List<MSAMDOption> getMSAMDOptions(Connection conn) {
        // msamd could have name or not
        String sql = "SELECT DISTINCT msamd, msamd_name FROM final_table WHERE action_taken='1'";
        List<MSAMDOption> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String code = rs.getString("msamd");
                String name = rs.getString("msamd_name");
                if (name != null && !name.trim().isEmpty()) {
                    list.add(new MSAMDOption(name, true));
                } else {
                    list.add(new MSAMDOption(code, false));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Sort by displayName
        list.sort(Comparator.comparing(o->o.displayName != null ? o.displayName : ""));
        return list;
    }

    static class MSAMDOption {
        String value; // either name or code
        boolean byName;
        String displayName;
        MSAMDOption(String val, boolean byName) {
            this.value = val;
            this.byName = byName;
            this.displayName = val;
        }
    }
}
