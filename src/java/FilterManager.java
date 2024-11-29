import java.util.List;
import java.util.ArrayList;

public class FilterManager {
    private List<String> filters = new ArrayList<>();

    // Add a new filter to the list
    public void addFilter(String filter) {
        filters.add(filter);
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
}
