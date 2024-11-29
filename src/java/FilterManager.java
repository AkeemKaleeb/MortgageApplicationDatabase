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
                List<String> msamdList = Arrays.asList(msamds.split("\\s*,\\s*"));
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
                List<String> countyList = Arrays.asList(counties.split("\\s*,\\s*"));
                addCountyFilter(countyList);
                break;
            }
            case 4: {
                System.out.println("Enter loan types (comma separated): ");
                String loanTypes = scanner.nextLine();
                List<String> loanTypeList = Arrays.asList(loanTypes.split("\\s*,\\s*"));
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
                List<String> loanPurposeList = Arrays.asList(loanPurposes.split("\\s*,\\s*"));
                addLoanPurposeFilter(loanPurposeList);
                break;
            }
            case 7: {
                System.out.println("Enter property types (comma separated): ");
                String propertyTypes = scanner.nextLine();
                List<String> propertyTypeList = Arrays.asList(propertyTypes.split("\\s*,\\s*"));
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
    public void removeFilter(String filterType) {
        filters.removeIf(filter -> filter.startsWith(filterType));
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
        if (filters.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", filters);
    }

    // MSAMD Filter
    public void addMSAMDFilter(List<String> msamds) {
        combineFilter("msamd IN", msamds);
    }

    // Add Income to Debt Ratio filter
    public void addIncomeToDebtRatioFilter(double minRatio, double maxRatio) {
        String newFilter = "applicant_income / loan_amount BETWEEN " + minRatio + " AND " + maxRatio;
        combineFilter("applicant_income / loan_amount BETWEEN", newFilter);
    }

    // Add County filter
    public void addCountyFilter(List<String> counties) {
        combineFilter("county IN", counties);
    }

    // Add Loan Type filter
    public void addLoanTypeFilter(List<String> loanTypes) {
        combineFilter("loan_type IN", loanTypes);
    }

    // Add Tract to MSAMD Income filter
    public void addTractToMSAMDIncomeFilter(double minIncome, double maxIncome) {
        String newFilter = "tract_to_msamd_income BETWEEN " + minIncome + " AND " + maxIncome;
        combineFilter("tract_to_msamd_income BETWEEN", newFilter);
    }

    // Add Loan Purpose filter
    public void addLoanPurposeFilter(List<String> loanPurposes) {
        combineFilter("loan_purpose IN", loanPurposes);
    }

    // Add Property Type filter
    public void addPropertyTypeFilter(List<String> propertyTypes) {
        combineFilter("property_type IN", propertyTypes);
    }

    // Add Owner Occupied filter
    public void addOwnerOccupiedFilter(boolean ownerOccupied) {
        String newFilter = "owner_occupied = " + (ownerOccupied ? "TRUE" : "FALSE");
        combineFilter("owner_occupied =", newFilter);
    }

    // Combine filters of the same type
    private void combineFilter(String filterType, List<String> newValues) {
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).startsWith(filterType)) {
                String existingValues = filters.get(i).substring(filters.get(i).indexOf("(") + 1, filters.get(i).indexOf(")"));
                List<String> existingList = new ArrayList<>(Arrays.asList(existingValues.split(", ")));
                existingList.addAll(newValues);
                filters.set(i, filterType + " (" + String.join(", ", existingList) + ")");
                return;
            }
        }
        filters.add(filterType + " (" + String.join(", ", newValues) + ")");
    }

    // Combine filters of the same type for non-list filters
    private void combineFilter(String filterType, String newFilter) {
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).startsWith(filterType)) {
                filters.set(i, newFilter);
                return;
            }
        }
        filters.add(newFilter);
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
