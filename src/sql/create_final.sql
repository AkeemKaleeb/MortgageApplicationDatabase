-- Drop existing tables if they exist
DROP TABLE IF EXISTS final_table CASCADE;

-- Create the final table with foreign keys
CREATE TABLE final_table (
    application_id INTEGER PRIMARY KEY,
    as_of_year VARCHAR(4),
    respondent_id VARCHAR(20),
    agency_code VARCHAR(10),
    loan_type VARCHAR(1),
    property_type VARCHAR(1),
    loan_purpose VARCHAR(1),
    owner_occupancy VARCHAR(1),
    loan_amount_000s VARCHAR(10),
    preapproval VARCHAR(1),
    action_taken VARCHAR(1),
    msamd VARCHAR(20),
    state_code VARCHAR(2),
    county_code VARCHAR(5),
    location_id INTEGER,
    applicant_ethnicity VARCHAR(1),
    co_applicant_ethnicity VARCHAR(1),
    applicant_race_1 VARCHAR(1),
    applicant_race_name_1 VARCHAR(100),
    applicant_race_2 VARCHAR(1),
    applicant_race_name_2 VARCHAR(100),
    applicant_race_3 VARCHAR(1),
    applicant_race_name_3 VARCHAR(100),
    applicant_race_4 VARCHAR(1),
    applicant_race_name_4 VARCHAR(100),
    applicant_race_5 VARCHAR(1),
    applicant_race_name_5 VARCHAR(100),
    co_applicant_race_1 VARCHAR(1),
    co_applicant_race_name_1 VARCHAR(100),
    co_applicant_race_2 VARCHAR(1),
    co_applicant_race_name_2 VARCHAR(100),
    co_applicant_race_3 VARCHAR(1),
    co_applicant_race_name_3 VARCHAR(100),
    co_applicant_race_4 VARCHAR(1),
    co_applicant_race_name_4 VARCHAR(100),
    co_applicant_race_5 VARCHAR(1),
    co_applicant_race_name_5 VARCHAR(100),
    applicant_sex VARCHAR(1),
    co_applicant_sex VARCHAR(1),
    applicant_income_000s VARCHAR(10),
    purchaser_type VARCHAR(1),
    rate_spread VARCHAR(20),
    hoepa_status VARCHAR(1),
    lien_status VARCHAR(1),
    edit_status VARCHAR(1),
    sequence_number VARCHAR(75),
    application_date_indicator VARCHAR(20)
);

-- Insert data into final_table
INSERT INTO final_table (
    application_id, as_of_year, respondent_id, agency_code, loan_type, property_type, loan_purpose, owner_occupancy, loan_amount_000s, preapproval, action_taken, msamd, state_code, county_code, location_id, applicant_ethnicity, co_applicant_ethnicity, applicant_race_1, applicant_race_name_1, applicant_race_2, applicant_race_name_2, applicant_race_3, applicant_race_name_3, applicant_race_4, applicant_race_name_4, applicant_race_5, applicant_race_name_5, co_applicant_race_1, co_applicant_race_name_1, co_applicant_race_2, co_applicant_race_name_2, co_applicant_race_3, co_applicant_race_name_3, co_applicant_race_4, co_applicant_race_name_4, co_applicant_race_5, co_applicant_race_name_5, applicant_sex, co_applicant_sex, applicant_income_000s, purchaser_type, rate_spread, hoepa_status, lien_status, edit_status, sequence_number, application_date_indicator
)
SELECT
    a.application_id, lt.as_of_year, a.respondent_id, ag.agency_code, lt.loan_type, pt.property_type, lp.loan_purpose, oo.owner_occupancy, lt.loan_amount_000s, pr.preapproval, at.action_taken, m.msamd, s.state_code, c.county_code, l.location_id, ae.applicant_ethnicity, cae.co_applicant_ethnicity, ar.applicant_race_1, ar.applicant_race_name_1, ar.applicant_race_2, ar.applicant_race_name_2, ar.applicant_race_3, ar.applicant_race_name_3, ar.applicant_race_4, ar.applicant_race_name_4, ar.applicant_race_5, ar.applicant_race_name_5, car.co_applicant_race_1, car.co_applicant_race_name_1, car.co_applicant_race_2, car.co_applicant_race_name_2, car.co_applicant_race_3, car.co_applicant_race_name_3, car.co_applicant_race_4, car.co_applicant_race_name_4, car.co_applicant_race_5, car.co_applicant_race_name_5, asx.applicant_sex, casx.co_applicant_sex, a.applicant_income_000s, pur.purchaser_type, lt.rate_spread, hs.hoepa_status, ls.lien_status, empty_table.edit_status, empty_table.sequence_number, empty_table.application_date_indicator
FROM applicant a
JOIN agency ag ON a.application_id = ag.application_id
JOIN loan_type lt ON a.application_id = lt.application_id
JOIN property_type pt ON a.application_id = pt.application_id
JOIN loan_purpose lp ON a.application_id = lp.application_id
JOIN owner_occupancy oo ON a.application_id = oo.application_id
JOIN preapproval pr ON a.application_id = pr.application_id
JOIN action_taken at ON a.application_id = at.application_id
JOIN msamd m ON a.application_id = m.application_id
JOIN state s ON a.application_id = s.application_id
JOIN county c ON a.application_id = c.application_id
JOIN location l ON a.application_id = l.application_id
JOIN applicant_ethnicity ae ON a.application_id = ae.application_id
JOIN co_applicant_ethnicity cae ON a.application_id = cae.application_id
JOIN applicant_race ar ON a.application_id = ar.application_id
JOIN co_applicant_race car ON a.application_id = car.application_id
JOIN applicant_sex asx ON a.application_id = asx.application_id
JOIN co_applicant_sex casx ON a.application_id = casx.application_id
JOIN purchaser_type pur ON a.application_id = pur.application_id
JOIN hoepa_status hs ON a.application_id = hs.application_id
JOIN lien_status ls ON a.application_id = ls.application_id
JOIN empty_table empty_table ON a.application_id = empty_table.application_id;

SELECT * FROM final_table LIMIT 10;