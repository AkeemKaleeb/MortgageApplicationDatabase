import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class FilterManager {
    private List<String> filters = new ArrayList<>();
    private List<String> filterDescriptions = new ArrayList<>();

    public void addFilterCondition(String sqlCondition, String description) {
        filters.add(sqlCondition);
        filterDescriptions.add(description);
    }

    public void removeFilter(String contains) {
        // Remove filters that contain a certain substring in SQL
        // Also remove corresponding description
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).contains(contains)) {
                toRemove.add(i);
            }
        }
        // remove in reverse order
        for (int i = toRemove.size()-1; i>=0; i--) {
            int idx = toRemove.get(i);
            filters.remove(idx);
            filterDescriptions.remove(idx);
        }
    }

    public void removeFilterByType(int filterID) {
        switch (filterID) {
            case 1:
                removeFilter("msamd");
                removeFilter("msamd_name");
                break;
            case 2:
                removeFilter("CAST(applicant_income_000s");
                break;
            case 3:
                removeFilter("county_name");
                break;
            case 4:
                removeFilter("loan_type_name");
                break;
            case 5:
                removeFilter("tract_to_msamd_income");
                break;
            case 6:
                removeFilter("loan_purpose_name");
                break;
            case 7:
                removeFilter("property_type_name");
                break;
            case 8:
                removeFilter("owner_occupancy_name");
                break;
            default:
                // do nothing
                break;
        }
    }

    public void clearFilters() {
        filters.clear();
        filterDescriptions.clear();
    }

    public String getFilterConditions() {
        if (filters.isEmpty()) {
            return "";
        }
        return String.join(" AND ", filters);
    }

    public String getFilterQuery() {
        // Always include action_taken='1'
        String baseCondition = "action_taken='1'";
        String conditions = getFilterConditions();

        if (conditions.isEmpty()) {
            return " WHERE " + baseCondition + " ";
        } else {
            return " WHERE " + baseCondition + " AND " + conditions + " ";
        }
    }

    // Display filters in a user-friendly manner
    public void displayActiveFilters() {
        if (filters.isEmpty()) {
            System.out.println("No active filters.");
        } else {
            System.out.println("Active filters:");
            for (String desc : filterDescriptions) {
                System.out.println(" - " + desc);
            }
        }
    }

    // Filter add methods (now we use name fields):
    public void addMSAMDFilter(List<String> msamdSelections, boolean byName) {
        // If byName = true, we filter by msamd_name, else by msamd code
        if (msamdSelections.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for (String m : msamdSelections) {
            quoted.add("'" + m + "'");
        }
        if (byName) {
            addFilterCondition("msamd_name IN (" + String.join(", ", quoted) + ")",
                    "MSAMD = " + String.join(" OR ", msamdSelections));
        } else {
            addFilterCondition("msamd IN (" + String.join(", ", quoted) + ")",
                    "MSAMD = " + String.join(" OR ", msamdSelections));
        }
    }

    public void addIncomeToDebtRatioFilter(Double minRatio, Double maxRatio) {
        String field = "CAST(applicant_income_000s AS NUMERIC) / CAST(loan_amount_000s AS NUMERIC)";
        List<String> cond = new ArrayList<>();
        String desc = "Income-to-Debt Ratio ";
        if (minRatio != null) {
            cond.add(field + " >= " + minRatio);
            desc += "Min: " + minRatio + " ";
        }
        if (maxRatio != null) {
            cond.add(field + " <= " + maxRatio);
            desc += "Max: " + maxRatio + " ";
        }
        if (!cond.isEmpty()) {
            addFilterCondition(String.join(" AND ", cond), desc.trim());
        }
    }

    public void addCountyFilter(List<String> counties) {
        if (counties.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for(String county : counties) {
            quoted.add("'" + county + "'");
        }
        String condition = "county_name IN (" + String.join(", ", quoted) + ")";
        addFilterCondition(condition, "County = " + String.join(" OR ", counties));
    }

    public void addLoanTypeFilter(List<String> loanTypes) {
        if (loanTypes.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for (String v : loanTypes) quoted.add("'" + v + "'");
        addFilterCondition("loan_type_name IN (" + String.join(", ", quoted) + ")",
                "Loan Type = " + String.join(" OR ", loanTypes));
    }

    public void addTractToMSAMDIncomeFilter(Double minIncome, Double maxIncome) {
        List<String> conditions = new ArrayList<>();
        String desc = "Tract to MSAMD Income ";
        if (minIncome != null) {
            conditions.add("tract_to_msamd_income::numeric >= " + minIncome);
            desc += "Min: " + minIncome + " ";
        }
        if (maxIncome != null) {
            conditions.add("tract_to_msamd_income::numeric <= " + maxIncome);
            desc += "Max: " + maxIncome + " ";
        }
        if (!conditions.isEmpty()) {
            addFilterCondition(String.join(" AND ", conditions), desc.trim());
        }
    }

    public void addLoanPurposeFilter(List<String> purposes) {
        if (purposes.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for (String v : purposes) quoted.add("'" + v + "'");
        addFilterCondition("loan_purpose_name IN (" + String.join(", ", quoted) + ")",
                "Loan Purpose = " + String.join(" OR ", purposes));
    }

    public void addPropertyTypeFilter(List<String> properties) {
        if (properties.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for (String v : properties) quoted.add("'" + v + "'");
        addFilterCondition("property_type_name IN (" + String.join(", ", quoted) + ")",
                "Property Type = " + String.join(" OR ", properties));
    }

    public void addOwnerOccupancyFilter(List<String> owners) {
        if (owners.isEmpty()) return;
        List<String> quoted = new ArrayList<>();
        for (String v : owners) quoted.add("'" + v + "'");
        addFilterCondition("owner_occupancy_name IN (" + String.join(", ", quoted) + ")",
                "Owner Occupancy = " + String.join(" OR ", owners));
    }
}
