import java.sql.*;
import java.util.Scanner;

public class MortgageCreator {
    private Connection conn;

    public MortgageCreator(Connection conn) {
        this.conn = conn;
    }

    public void addNewMortgage() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter applicant income (integer): ");
        String incomeStr = scanner.nextLine().trim();

        System.out.println("Enter loan amount (in thousands): ");
        String loanAmountStr = scanner.nextLine().trim();

        System.out.println("Enter msamd: ");
        String msamd = scanner.nextLine().trim();

        System.out.println("Enter applicant sex (M or F): ");
        String applicantSex = scanner.nextLine().trim();
        String applicantSexCode = applicantSex.equalsIgnoreCase("M") ? "1" : "2";

        System.out.println("Enter loan type (e.g. 1,2,3,4): ");
        String loanType = scanner.nextLine().trim();

        System.out.println("Enter applicant ethnicity (1=Hispanic or Latino, 2=Not Hispanic): ");
        String ethnicity = scanner.nextLine().trim();

        int locationId = findLocationIdByMsamd(msamd);
        if (locationId == -1) {
            System.out.println("No location found for given msamd. Cannot add mortgage.");
            return;
        }


        String indexSql = "SELECT MAX(application_id) AS current_max_id FROM final_table";
        int current_max_id = 0;
        try (PreparedStatement ps = conn.prepareStatement(indexSql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                current_max_id = rs.getInt("current_max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert a new row into final_table
        String insertSql = "INSERT INTO final_table(application_id, as_of_year, respondent_id, agency_code, loan_type, property_type, loan_purpose, owner_occupancy, loan_amount_000s, preapproval, action_taken, msamd, state_code, county_code, location_id, applicant_ethnicity, co_applicant_ethnicity, applicant_race_1, applicant_race_name_1, applicant_race_2, applicant_race_name_2, applicant_race_3, applicant_race_name_3, applicant_race_4, applicant_race_name_4, applicant_race_5, applicant_race_name_5, co_applicant_race_1, co_applicant_race_name_1, co_applicant_race_2, co_applicant_race_name_2, co_applicant_race_3, co_applicant_race_name_3, co_applicant_race_4, co_applicant_race_name_4, co_applicant_race_5, co_applicant_race_name_5, applicant_sex, co_applicant_sex, applicant_income_000s, purchaser_type, rate_spread, hoepa_status, lien_status, edit_status, sequence_number, application_date_indicator) " +
                "VALUES(?, '2017','NEWAPP','1',?,'1','1','1',?, '1','1',?, 'NJ','001',?, ?, '2','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided','5','Not provided', '5', 'Not Provided', ?, '5', ?, '0','NA','1','1','1','000001','20210101') RETURNING application_id;";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, current_max_id + 1);
            ps.setString(2, loanType);
            ps.setString(3, loanAmountStr);
            ps.setString(4, msamd);
            ps.setInt(5, locationId);
            ps.setString(6, ethnicity);
            ps.setString(7, applicantSexCode);
            ps.setString(8, incomeStr);

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
}
