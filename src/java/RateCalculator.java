import java.sql.ResultSet;
import java.sql.SQLException;

public class RateCalculator {

    // Calculate the rate of the mortgage based on the loan amount and rate
    public double calculateRate(ResultSet result) throws SQLException {
        double totalLoanAmount = 0;
        double weightedRateSum = 0;

        while(result.next()) {
            double loanAmount = result.getDouble("loan_amount");
            double rate = result.getDouble("rate");

            totalLoanAmount += loanAmount;
            weightedRateSum += loanAmount * rate;
        }
        return weightedRateSum / totalLoanAmount;
    }
}
