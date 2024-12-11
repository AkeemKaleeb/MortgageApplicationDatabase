import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MortgageCreator {
    private Connection conn;

    public MortgageCreator(Connection conn) {
        this.conn = conn;
    }

    public void addNewMortgage() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter applicant income (in thousands): ");
        String incomeStr = scanner.nextLine().trim();

        System.out.println("Enter loan amount (in thousands): ");
        String loanAmountStr = scanner.nextLine().trim();

        // Select MSAMD from list
        List<MSAMDOption> msamdOptions = getMSAMDOptions();
        if (msamdOptions.isEmpty()) {
            System.out.println("No MSAMD available. Cannot add mortgage.");
            return;
        }
        System.out.println("Select MSAMD:");
        for (int i=0; i<msamdOptions.size(); i++) {
            System.out.println((i+1) + ". " + msamdOptions.get(i).displayName);
        }
        int msamdChoice = Integer.parseInt(scanner.nextLine().trim());
        MSAMDOption chosenMsamd = msamdOptions.get(msamdChoice-1);
        String chosenMsamdCode = getMsamdCode(chosenMsamd);

        // Applicant Sex
        // We have applicant_sex_name and applicant_sex in final_table. Let's pick from distinct applicant_sex_name
        // Typically "Male", "Female", "Information not provided", "Not applicable"
        List<String> sexOptions = getDistinctValues("applicant_sex_name");
        System.out.println("Select Applicant Sex:");
        for (int i=0; i<sexOptions.size(); i++) {
            System.out.println((i+1) + ". " + sexOptions.get(i));
        }
        int sexChoice = Integer.parseInt(scanner.nextLine().trim());
        String chosenSexName = sexOptions.get(sexChoice-1);
        String chosenSexCode = getCodeForName("applicant_sex", "applicant_sex_name", chosenSexName);

        // Loan Type
        List<String> loanTypeOptions = getDistinctValues("loan_type_name");
        System.out.println("Select Loan Type:");
        for (int i=0; i<loanTypeOptions.size(); i++) {
            System.out.println((i+1) + ". " + loanTypeOptions.get(i));
        }
        int ltChoice = Integer.parseInt(scanner.nextLine().trim());
        String chosenLoanTypeName = loanTypeOptions.get(ltChoice-1);
        String chosenLoanTypeCode = getCodeForName("loan_type", "loan_type_name", chosenLoanTypeName);

        // Ethnicity
        List<String> ethOptions = getDistinctValues("applicant_ethnicity_name");
        if (ethOptions.isEmpty()) {
            System.out.println("No ethnicity data found, defaulting to Not Hispanic (2)");
            ethOptions.add("Not Hispanic or Latino");
        }
        System.out.println("Select Applicant Ethnicity:");
        for (int i=0; i<ethOptions.size(); i++) {
            System.out.println((i+1) + ". " + ethOptions.get(i));
        }
        int ethChoice = Integer.parseInt(scanner.nextLine().trim());
        String chosenEthName = ethOptions.get(ethChoice-1);
        String chosenEthCode = getCodeForName("applicant_ethnicity", "applicant_ethnicity_name", chosenEthName);

        int locationId = findLocationIdByMsamd(chosenMsamdCode);
        if (locationId == -1) {
            System.out.println("No location found for given msamd. Cannot add mortgage.");
            return;
        }

        int current_max_id = 0;
        String indexSql = "SELECT MAX(application_id) AS current_max_id FROM final_table";
        try (PreparedStatement ps = conn.prepareStatement(indexSql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                current_max_id = rs.getInt("current_max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert a new row into final_table
        // Some fields left null or default, simplifying as per instructions
        String insertSql = "INSERT INTO final_table(application_id, as_of_year, respondent_id, agency_code, loan_type, loan_type_name, property_type, property_type_name, loan_purpose, loan_purpose_name, owner_occupancy, owner_occupancy_name, loan_amount_000s, preapproval, action_taken, msamd, msamd_name, state_code, county_code, county_name, location_id, applicant_ethnicity, applicant_ethnicity_name, co_applicant_ethnicity, co_applicant_ethnicity_name, applicant_race_1, applicant_race_name_1, applicant_race_2, applicant_race_name_2, applicant_race_3, applicant_race_name_3, applicant_race_4, applicant_race_name_4, applicant_race_5, applicant_race_name_5, co_applicant_race_1, co_applicant_race_name_1, co_applicant_race_2, co_applicant_race_name_2, co_applicant_race_3, co_applicant_race_name_3, co_applicant_race_4, co_applicant_race_name_4, co_applicant_race_5, co_applicant_race_name_5, applicant_sex, applicant_sex_name, co_applicant_sex, co_applicant_sex_name, applicant_income_000s, purchaser_type, purchaser_type_name, rate_spread, hoepa_status, lien_status, edit_status, sequence_number, application_date_indicator) " +
                "VALUES(?, '2017','NEWAPP','1',?, ?, '1','Single Family','1','Home purchase','1','Owner-occupied',?, '1','1',?, ?, 'NJ','001','Unknown',?, ?, ?, '2','Not Hispanic', '5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not Provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided', ?, ?, '5','Not Provided', ?, '5', 'Private Investor','2.50','0','1','1','000001','20210101') RETURNING application_id;";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, current_max_id + 1);
            ps.setString(2, chosenLoanTypeCode);
            ps.setString(3, chosenLoanTypeName);
            ps.setString(4, loanAmountStr);
            ps.setString(5, chosenMsamdCode);
            ps.setString(6, chosenMsamd.byName ? chosenMsamd.value : null);
            ps.setInt(7, locationId);
            ps.setString(8, chosenEthCode);
            ps.setString(9, chosenEthName);
            ps.setString(10, chosenSexCode);
            ps.setString(11, chosenSexName);
            ps.setString(12, incomeStr);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int appId = rs.getInt("application_id");
                System.out.println("New mortgage added with application_id: " + appId);
            } else {
                System.out.println("Failed to insert new mortgage.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int findLocationIdByMsamd(String msamd) {
        String sql = "SELECT location_id FROM location WHERE msamd = ? LIMIT 1;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, msamd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("location_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<String> getDistinctValues(String column) {
        String sql = "SELECT DISTINCT " + column + " FROM final_table WHERE " + column + " IS NOT NULL AND " + column + " <> '' ORDER BY " + column;
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

    // Get code for given name from final_table (take first match)
    // columnForCode: e.g. "applicant_sex"
    // columnForName: e.g. "applicant_sex_name"
    private String getCodeForName(String columnForCode, String columnForName, String name) {
        String sql = "SELECT DISTINCT " + columnForCode + " FROM final_table WHERE " + columnForName + " = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(columnForCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // default fallback
        return "5";
    }

    private List<MSAMDOption> getMSAMDOptions() {
        String sql = "SELECT DISTINCT msamd, msamd_name FROM final_table WHERE action_taken='1'";
        List<MSAMDOption> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String code = rs.getString("msamd");
                String name = rs.getString("msamd_name");
                if (name != null && !name.trim().isEmpty()) {
                    list.add(new MSAMDOption(name, true, name));
                } else {
                    list.add(new MSAMDOption(code, false, code));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.sort(Comparator.comparing(o->o.displayName != null ? o.displayName : ""));
        return list;
    }

    private String getMsamdCode(MSAMDOption opt) {
        if (opt.byName) {
            // find code by name
            String sql = "SELECT DISTINCT msamd FROM final_table WHERE msamd_name = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, opt.value);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("msamd");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return opt.value; // fallback
        } else {
            return opt.value; // it's already a code
        }
    }

    static class MSAMDOption {
        String value; 
        boolean byName;
        String displayName;
        MSAMDOption(String val, boolean byName, String disp) {
            this.value = val;
            this.byName = byName;
            this.displayName = disp;
        }
    }
}
