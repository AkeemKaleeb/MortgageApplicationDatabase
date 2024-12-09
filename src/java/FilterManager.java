import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FilterManager {
    private List<String> filters = new ArrayList<>();

    public void addFilter(int filterID) {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        switch (filterID) {
            case 1: { // MSAMD
                System.out.println("Enter MSAMDs (comma separated): ");
                String msamds = scanner.nextLine();
                List<String> msamdList = Arrays.asList(msamds.split("\\s*,\\s*"));
                addMSAMDFilter(msamdList);
                break;
            }
            case 2: { // Income to Debt Ratio
                System.out.println("Enter minimum income to debt ratio (or blank): ");
                String minStr = scanner.nextLine().trim();
                Double minRatio = minStr.isEmpty() ? null : Double.parseDouble(minStr);

                System.out.println("Enter maximum income to debt ratio (or blank): ");
                String maxStr = scanner.nextLine().trim();
                Double maxRatio = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);

                addIncomeToDebtRatioFilter(minRatio, maxRatio);
                break;
            }
            case 3: { // County
                System.out.println("Enter counties (comma separated): ");
                String counties = scanner.nextLine();
                List<String> countyList = Arrays.asList(counties.split("\\s*,\\s*"));
                addCountyFilter(countyList);
                break;
            }
            case 4: { // Loan Type
                System.out.println("Enter loan types (comma separated): ");
                String loanTypes = scanner.nextLine();
                List<String> loanTypeList = Arrays.asList(loanTypes.split("\\s*,\\s*"));
                addLoanTypeFilter(loanTypeList);
                break;
            }
            case 5: { // Tract to MSAMD Income
                System.out.println("Enter minimum tract_to_msamd_income (or blank): ");
                String minStr = scanner.nextLine().trim();
                Double minIncome = minStr.isEmpty() ? null : Double.parseDouble(minStr);

                System.out.println("Enter maximum tract_to_msamd_income (or blank): ");
                String maxStr = scanner.nextLine().trim();
                Double maxIncome = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);

                addTractToMSAMDIncomeFilter(minIncome, maxIncome);
                break;
            }
            case 6: { // Loan Purpose
                System.out.println("Enter loan purposes (comma separated): ");
                String input = scanner.nextLine();
                List<String> purposeList = Arrays.asList(input.split("\\s*,\\s*"));
                addLoanPurposeFilter(purposeList);
                break;
            }
            case 7: { // Property Type
                System.out.println("Enter property types (comma separated): ");
                String input = scanner.nextLine();
                List<String> propertyTypeList = Arrays.asList(input.split("\\s*,\\s*"));
                addPropertyTypeFilter(propertyTypeList);
                break;
            }
            case 8: { // Owner Occupied
                System.out.println("Enter owner occupancy values (e.g. '1' for owner-occupied) (comma separated): ");
                String input = scanner.nextLine();
                List<String> ownerList = Arrays.asList(input.split("\\s*,\\s*"));
                addOwnerOccupancyFilter(ownerList);
                break;
            }
            default: {
                System.out.println("Invalid filter option");
                break;
            }
        }
    }

    public void removeFilter(String filterType) {
        filters.removeIf(filter -> filter.contains(filterType));
    }

    public void removeFilterByType(int filterID) {
        switch (filterID) {
            case 1:
                removeFilter("msamd");
                break;
            case 2:
                removeFilter("applicant_income_000s");
                break;
            case 3:
                removeFilter("county_name");
                break;
            case 4:
                removeFilter("loan_type");
                break;
            case 5:
                removeFilter("tract_to_msamd_income");
                break;
            case 6:
                removeFilter("loan_purpose");
                break;
            case 7:
                removeFilter("property_type");
                break;
            case 8:
                removeFilter("owner_occupancy");
                break;
            default:
                System.out.println("Invalid filter option");
                break;
        }
    }

    public void clearFilters() {
        filters.clear();
    }

    public String getFilterConditions() {
        if (filters.isEmpty()) {
            return "";
        }
        return String.join(" AND ", filters);
    }

    public String getFilterQuery() {
        // Always include action_taken='1' (Loan originated)
        String baseCondition = "action_taken='1'";
        String conditions = getFilterConditions();

        if (conditions.isEmpty()) {
            return " WHERE " + baseCondition + " ";
        } else {
            return " WHERE " + baseCondition + " AND " + conditions + " ";
        }
    }

    // Filter methods
    private void addInFilter(String column, List<String> values) {
        List<String> quoted = new ArrayList<>();
        for (String v : values) {
            quoted.add("'" + v + "'");
        }
        filters.add(column + " IN (" + String.join(", ", quoted) + ")");
    }

    public void addMSAMDFilter(List<String> msamds) {
        addInFilter("msamd", msamds);
    }

    public void addIncomeToDebtRatioFilter(Double minRatio, Double maxRatio) {
        // Income to debt ratio = (applicant_income_000s * 1000)/(loan_amount_000s * 1000)
        String field = "applicant_income_000s / loan_amount_000s";
        List<String> conditions = new ArrayList<>();
        if (minRatio != null) conditions.add(field + " >= " + minRatio);
        if (maxRatio != null) conditions.add(field + " <= " + maxRatio);
        if (!conditions.isEmpty()) {
            filters.add(String.join(" AND ", conditions));
        }
    }

    public void addCountyFilter(List<String> counties) {
        addInFilter("county_name", counties);
    }

    public void addLoanTypeFilter(List<String> loanTypes) {
        addInFilter("loan_type", loanTypes);
    }

    public void addTractToMSAMDIncomeFilter(Double minIncome, Double maxIncome) {
        List<String> conditions = new ArrayList<>();
        if (minIncome != null) conditions.add("tract_to_msamd_income::numeric >= " + minIncome);
        if (maxIncome != null) conditions.add("tract_to_msamd_income::numeric <= " + maxIncome);
        if (!conditions.isEmpty()) filters.add(String.join(" AND ", conditions));
    }

    public void addLoanPurposeFilter(List<String> loanPurposes) {
        addInFilter("loan_purpose", loanPurposes);
    }

    public void addPropertyTypeFilter(List<String> propertyTypes) {
        addInFilter("property_type", propertyTypes);
    }

    public void addOwnerOccupancyFilter(List<String> owner_occupancies) {
        addInFilter("owner_occupancy", owner_occupancies);
    }

    public void displayActiveFilters() {
        if (filters.isEmpty()) {
            System.out.println("No active filters.");
        } else {
            System.out.println("Active filters:");
            for (String filter : filters) {
                System.out.println(" - " + filter);
            }
        }
    }
}
