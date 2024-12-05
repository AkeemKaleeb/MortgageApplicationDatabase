import java.sql.*;

public class RateCalculator {
    private Connection conn;
    private static final double BASE_RATE = 2.33;

    public RateCalculator(Connection conn) {
        this.conn = conn;
    }

    public RateResult calculateRate(String filterQuery) {
        String sql = "SELECT lien_status, rate_spread, loan_amount_000s FROM final_table " + filterQuery + ";";

        double totalLoanAmount = 0.0;
        double weightedRateSum = 0.0;
        boolean anyRows = false;

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                anyRows = true;
                String lienStatusStr = rs.getString("lien_status");
                String rateSpreadStr = rs.getString("rate_spread");
                String loanAmountStr = rs.getString("loan_amount_000s");

                double loanAmount = 0.0;
                try {
                    loanAmount = Double.parseDouble(loanAmountStr) * 1000.0;
                } catch (NumberFormatException e) {
                    continue;
                }

                int lienStatus = 0;
                try {
                    lienStatus = Integer.parseInt(lienStatusStr);
                } catch (NumberFormatException e) {
                    // default is 0
                }

                Double rateSpread = null;
                if (rateSpreadStr != null && !rateSpreadStr.trim().isEmpty() && !rateSpreadStr.equalsIgnoreCase("NA")) {
                    try {
                        rateSpread = Double.parseDouble(rateSpreadStr);
                    } catch (NumberFormatException e) {
                        rateSpread = null;
                    }
                }

                double threshold = (lienStatus == 1) ? 1.5 : (lienStatus == 2 ? 3.5 : 1.5);

                double finalRate;
                if (rateSpread == null || rateSpread < threshold) {
                    finalRate = BASE_RATE + threshold;
                } else {
                    finalRate = BASE_RATE + rateSpread;
                }

                weightedRateSum += finalRate * loanAmount;
                totalLoanAmount += loanAmount;
            }

            if (!anyRows || totalLoanAmount == 0.0) {
                return new RateResult(0, 0, true);
            }

            double weightedAvgRate = weightedRateSum / totalLoanAmount;
            return new RateResult(totalLoanAmount, weightedAvgRate, false);
        } catch (SQLException e) {
            e.printStackTrace();
            return new RateResult(0, 0, true);
        }
    }

    public static class RateResult {
        public double totalLoanAmount;
        public double rate;
        public boolean empty;

        public RateResult(double totalLoanAmount, double rate, boolean empty) {
            this.totalLoanAmount = totalLoanAmount;
            this.rate = rate;
            this.empty = empty;
        }
    }
}
