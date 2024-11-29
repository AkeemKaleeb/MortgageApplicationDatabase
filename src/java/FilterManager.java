import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FilterManager {
    private List<String> filters = new ArrayList<>();

    // Add a new filter to the list
    public void addFilter(int filterID) {
        Scanner scanner = new Scanner(System.in);
        switch (filterID) {
            case 1: {
                System.out.println("Enter MSAMDs (comma separated): ");
                String msamds = scanner.nextLine();
                List<String> msamdList = Arrays.asList(msamds.split(","));
                addMSAMDFilter(msamdList);
                break;
            }
            case 2: {
                System.out.println("Enter minimum income to debt ratio: ");
                double minRatio = scanner.nextDouble();
                System.out.println("Enter maximum income to debt ratio: ");
                double maxRatio = scanner.nextDouble();
                addIncomeToDebtRatioFilter(minRatio, maxRatio);
                break;
            }
            case 3: {
                System.out.println("Enter counties (comma separated): ");
                String counties = scanner.nextLine();
                List<String> countyList = Arrays.asList(counties.split(","));
                addCountyFilter(countyList);
                break;
            }
            case 4: {
                System.out.println("Enter loan types (comma separated): ");
                String loanTypes = scanner.nextLine();
                List<String> loanTypeList = Arrays.asList(loanTypes.split(","));
                addLoanTypeFilter(loanTypeList);
                break;
            }
            case 5: {
                System.out.println("Enter minimum tract to MSAMD income: ");
                double minIncome = scanner.nextDouble();
                System.out.println("Enter maximum tract to MSAMD income: ");
                double maxIncome = scanner.nextDouble();
                addTractToMSAMDIncomeFilter(minIncome, maxIncome);
                break;
            }
            case 6: {
                System.out.println("Enter loan purposes (comma separated): ");
                String loanPurposes = scanner.nextLine();
                List<String> loanPurposeList = Arrays.asList(loanPurposes.split(","));
                addLoanPurposeFilter(loanPurposeList);
                break;
            }
            case 7: {
                System.out.println("Enter property types (comma separated): ");
                String propertyTypes = scanner.nextLine();
                List<String> propertyTypeList = Arrays.asList(propertyTypes.split(","));
                addPropertyTypeFilter(propertyTypeList);
                break;
            }
            case 8: {
                System.out.println("Enter owner occupied (true/false): ");
                boolean ownerOccupied = scanner.nextBoolean();
                addOwnerOccupiedFilter(ownerOccupied);
                break;
            }
            default: {
                System.out.println("Invalid filter option");
                break;
            }
        }
    }

    // Remove a filter from the list
    public void removeFilter(String filter) {
        filters.remove(filter);
    }

    // Remove all filters from the list
    public void clearFilters() {
        filters.clear();
    }

    // Get the list of filters
    public List<String> getFilters() {
        return filters;
    }

    // Get the filters as an SQL query
    public String getFilterQuery() {
        if(filters.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", filters);
    }

    // MSAMD Filter
    public void addMSAMDFilter(List<String> msamds) {
        String filter = "msamd IN (" + String.join(", ", msamds) + ")";
        filters.add(filter);
    }

    // Add Income to Debt Ratio filter
    public void addIncomeToDebtRatioFilter(double minRatio, double maxRatio) {
        String filter = "applicant_income / loan_amount BETWEEN " + minRatio + " AND " + maxRatio;
        filters.add(filter);
    }

    // Add County filter
    public void addCountyFilter(List<String> counties) {
        String filter = "county IN (" + String.join(", ", counties) + ")";
        filters.add(filter);
    }

    // Add Loan Type filter
    public void addLoanTypeFilter(List<String> loanTypes) {
        String filter = "loan_type IN (" + String.join(", ", loanTypes) + ")";
        filters.add(filter);
    }

    // Add Tract to MSAMD Income filter
    public void addTractToMSAMDIncomeFilter(double minIncome, double maxIncome) {
        String filter = "tract_to_msamd_income BETWEEN " + minIncome + " AND " + maxIncome;
        filters.add(filter);
    }

    // Add Loan Purpose filter
    public void addLoanPurposeFilter(List<String> loanPurposes) {
        String filter = "loan_purpose IN (" + String.join(", ", loanPurposes) + ")";
        filters.add(filter);
    }

    // Add Property Type filter
    public void addPropertyTypeFilter(List<String> propertyTypes) {
        String filter = "property_type IN (" + String.join(", ", propertyTypes) + ")";
        filters.add(filter);
    }

    // Add Owner Occupied filter
    public void addOwnerOccupiedFilter(boolean ownerOccupied) {
        String filter = "owner_occupied = " + (ownerOccupied ? "TRUE" : "FALSE");
        filters.add(filter);
    }

    // Display active filters
    public void displayActiveFilters() {
        if (filters.isEmpty()) {
            System.out.println("No active filters.");
        } else {
            System.out.println("Active filters:");
            for (String filter : filters) {
                System.out.println(filter);
            }
        }
    }
}
