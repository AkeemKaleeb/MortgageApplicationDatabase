DROP TABLE IF EXISTS preliminary CASCADE;
CREATE TABLE preliminary (
    as_of_year VARCHAR(4),
    respondent_id VARCHAR(20),
    agency_name VARCHAR(75),
    agency_abbr VARCHAR(10),
    agency_code VARCHAR(10),
    loan_type_name VARCHAR(75),
    loan_type VARCHAR(1),
    property_type_name VARCHAR(100),
    property_type VARCHAR(1),
    loan_purpose_name VARCHAR(75),
    loan_purpose VARCHAR(1),
    owner_occupancy_name VARCHAR(100),
    owner_occupancy VARCHAR(10),
    loan_amount_000s VARCHAR(10),
    preapproval_name VARCHAR(75),
    preapproval VARCHAR(1),
    action_taken_name VARCHAR(75),
    action_taken VARCHAR(1),
    msamd_name VARCHAR(75),
    msamd VARCHAR(20),
    state_name VARCHAR(25),
    state_abbr VARCHAR(2),
    state_code VARCHAR(2),
    county_name VARCHAR(50),
    county_code VARCHAR(2),
    census_tract_number VARCHAR(20),
    applicant_ethnicity_name VARCHAR(100),
    applicant_ethnicity VARCHAR(1),
    co_applicant_ethnicity_name VARCHAR(100),
    co_applicant_ethnicity VARCHAR(1),
    applicant_race_name_1 VARCHAR(100),
    applicant_race_1 VARCHAR(1),
    applicant_race_name_2 VARCHAR(100),
    applicant_race_2 VARCHAR(1),
    applicant_race_name_3 VARCHAR(100),
    applicant_race_3 VARCHAR(1),
    applicant_race_name_4 VARCHAR(100),
    applicant_race_4 VARCHAR(1),
    applicant_race_name_5 VARCHAR(100),
    applicant_race_5 VARCHAR(1),
    co_applicant_race_name_1 VARCHAR(100),
    co_applicant_race_1 VARCHAR(1),
    co_applicant_race_name_2 VARCHAR(100),
    co_applicant_race_2 VARCHAR(1),
    co_applicant_race_name_3 VARCHAR(100),
    co_applicant_race_3 VARCHAR(1),
    co_applicant_race_name_4 VARCHAR(100),
    co_applicant_race_4 VARCHAR(1),
    co_applicant_race_name_5 VARCHAR(100),
    co_applicant_race_5 VARCHAR(1),
    applicant_sex_name VARCHAR(100),
    applicant_sex VARCHAR(1),
    co_applicant_sex_name VARCHAR(100),
    co_applicant_sex VARCHAR(1),
    applicant_income_000s VARCHAR(10),
    purchaser_type_name VARCHAR(100),
    purchaser_type VARCHAR(1),
    denial_reason_name_1 VARCHAR(75),
    denial_reason_1 VARCHAR(1),
    denial_reason_name_2 VARCHAR(75),
    denial_reason_2 VARCHAR(1),
    denial_reason_name_3 VARCHAR(75),
    denial_reason_3 VARCHAR(1),
    rate_spread VARCHAR(20),
    hoepa_status_name VARCHAR(75),
    hoepa_status VARCHAR(1),
    lien_status_name VARCHAR(75),
    lien_status VARCHAR(1),
    edit_status_name VARCHAR(75),
    edit_status VARCHAR(1),
    sequence_number VARCHAR(10),
    population VARCHAR(20),
    minority_population VARCHAR(20),
    hud_median_family_income VARCHAR(20),
    tract_to_msamd_income VARCHAR(20),
    number_of_owner_occupied_units VARCHAR(20),
    number_of_1_to_4_family_units VARCHAR(20),
    application_date_indicator VARCHAR(20)
);

COPY preliminary 
FROM '/Users/haris/working_dir/Mortgage-Application-Database/hmda_2017_nj_all-records_labels.csv'
DELIMITER ',' CSV HEADER QUOTE '"';

-- Drop existing tables if they exist
DROP TABLE IF EXISTS applicant, empty_table, agency, loan_type, property_type, loan_purpose, owner_occupancy, preapproval, action_taken, msamd, state, county, applicant_ethnicity, co_applicant_ethnicity, applicant_race, co_applicant_race, applicant_sex, co_applicant_sex, purchaser_type, denial_reason, hoepa_status, lien_status, edit_status, location, final_table CASCADE;

-- Alter preliminary table to add application_id SERIAL PRIMARY KEY
ALTER TABLE preliminary DROP COLUMN IF EXISTS application_id;
ALTER TABLE preliminary ADD COLUMN application_id SERIAL PRIMARY KEY;

-- Create normalized tables
CREATE TABLE agency (
    application_id INTEGER PRIMARY KEY,
    agency_code VARCHAR(10),
    agency_name VARCHAR(75),
    agency_abbr VARCHAR(10)
);

CREATE TABLE applicant (
    application_id INTEGER PRIMARY KEY,
    respondent_id VARCHAR(20),
    applicant_income_000s VARCHAR(10)
);

CREATE TABLE loan_type (
    application_id INTEGER PRIMARY KEY,
    as_of_year VARCHAR(4),
    loan_type VARCHAR(1),
    loan_type_name VARCHAR(75),
    loan_amount_000s VARCHAR(10),
    rate_spread VARCHAR(20)
);

CREATE TABLE property_type (
    application_id INTEGER PRIMARY KEY,
    property_type VARCHAR(1),
    property_type_name VARCHAR(100)
);

CREATE TABLE loan_purpose (
    application_id INTEGER PRIMARY KEY,
    loan_purpose VARCHAR(1),
    loan_purpose_name VARCHAR(75)
);

CREATE TABLE owner_occupancy (
    application_id INTEGER PRIMARY KEY,
    owner_occupancy VARCHAR(10),
    owner_occupancy_name VARCHAR(100)
);

CREATE TABLE preapproval (
    application_id INTEGER PRIMARY KEY,
    preapproval VARCHAR(1),
    preapproval_name VARCHAR(75)
);

CREATE TABLE action_taken (
    application_id INTEGER PRIMARY KEY,
    action_taken VARCHAR(1),
    action_taken_name VARCHAR(75)
);

CREATE TABLE msamd (
    application_id INTEGER PRIMARY KEY,
    msamd VARCHAR(20),
    msamd_name VARCHAR(75)
);

CREATE TABLE state (
    application_id INTEGER PRIMARY KEY,
    state_code VARCHAR(2),
    state_name VARCHAR(25),
    state_abbr VARCHAR(2)
);

CREATE TABLE county (
    application_id INTEGER PRIMARY KEY,
    county_code VARCHAR(2),
    county_name VARCHAR(50)
);

CREATE TABLE applicant_ethnicity (
    application_id INTEGER PRIMARY KEY,
    applicant_ethnicity VARCHAR(1),
    applicant_ethnicity_name VARCHAR(100)
);

CREATE TABLE co_applicant_ethnicity (
    application_id INTEGER PRIMARY KEY,
    co_applicant_ethnicity VARCHAR(1),
    co_applicant_ethnicity_name VARCHAR(100)
);

CREATE TABLE applicant_race (
    application_id INTEGER PRIMARY KEY,
    applicant_race_1 VARCHAR(1),
    applicant_race_name_1 VARCHAR(100),
    applicant_race_2 VARCHAR(1),
    applicant_race_name_2 VARCHAR(100),
    applicant_race_3 VARCHAR(1),
    applicant_race_name_3 VARCHAR(100),
    applicant_race_4 VARCHAR(1),
    applicant_race_name_4 VARCHAR(100),
    applicant_race_5 VARCHAR(1),
    applicant_race_name_5 VARCHAR(100)
);

CREATE TABLE co_applicant_race (
    application_id INTEGER PRIMARY KEY,
    co_applicant_race_1 VARCHAR(1),
    co_applicant_race_name_1 VARCHAR(100),
    co_applicant_race_2 VARCHAR(1),
    co_applicant_race_name_2 VARCHAR(100),
    co_applicant_race_3 VARCHAR(1),
    co_applicant_race_name_3 VARCHAR(100),
    co_applicant_race_4 VARCHAR(1),
    co_applicant_race_name_4 VARCHAR(100),
    co_applicant_race_5 VARCHAR(1),
    co_applicant_race_name_5 VARCHAR(100)
);

CREATE TABLE applicant_sex (
    application_id INTEGER PRIMARY KEY,
    applicant_sex VARCHAR(1),
    applicant_sex_name VARCHAR(100)
);

CREATE TABLE co_applicant_sex (
    application_id INTEGER PRIMARY KEY,
    co_applicant_sex VARCHAR(1),
    co_applicant_sex_name VARCHAR(100)
);

CREATE TABLE purchaser_type (
    application_id INTEGER PRIMARY KEY,
    purchaser_type VARCHAR(50),
    purchaser_type_name VARCHAR(100)
);

CREATE TABLE denial_reason (
    application_id INTEGER PRIMARY KEY,
    denial_reason_1 VARCHAR(1),
    denial_reason_name_1 VARCHAR(75),
    denial_reason_2 VARCHAR(1),
    denial_reason_name_2 VARCHAR(75),
    denial_reason_3 VARCHAR(1),
    denial_reason_name_3 VARCHAR(75)
);

CREATE TABLE hoepa_status (
    application_id INTEGER PRIMARY KEY,
    hoepa_status VARCHAR(1),
    hoepa_status_name VARCHAR(75)
);

CREATE TABLE lien_status (
    application_id INTEGER PRIMARY KEY,
    lien_status VARCHAR(1),
    lien_status_name VARCHAR(75)
);

CREATE TABLE empty_table (
    application_id INTEGER PRIMARY KEY,
    edit_status VARCHAR(1),
    edit_status_name VARCHAR(75),
    application_date_indicator VARCHAR(20),
    sequence_number VARCHAR(10)
);

-- Insert data into normalized tables
INSERT INTO agency (application_id, agency_code, agency_name, agency_abbr)
SELECT application_id, agency_code, agency_name, agency_abbr FROM preliminary;

INSERT INTO loan_type (application_id, as_of_year, loan_type, loan_type_name, loan_amount_000s, rate_spread)
SELECT application_id, as_of_year, loan_type, loan_type_name, loan_amount_000s, rate_spread FROM preliminary;

INSERT INTO applicant (application_id, respondent_id, applicant_income_000s)
SELECT application_id, respondent_id, applicant_income_000s FROM preliminary;

INSERT INTO property_type (application_id, property_type, property_type_name)
SELECT application_id, property_type, property_type_name FROM preliminary;

INSERT INTO loan_purpose (application_id, loan_purpose, loan_purpose_name)
SELECT application_id, loan_purpose, loan_purpose_name FROM preliminary;

INSERT INTO owner_occupancy (application_id, owner_occupancy, owner_occupancy_name)
SELECT application_id, owner_occupancy, owner_occupancy_name FROM preliminary;

INSERT INTO preapproval (application_id, preapproval, preapproval_name)
SELECT application_id, preapproval, preapproval_name FROM preliminary;

INSERT INTO action_taken (application_id, action_taken, action_taken_name)
SELECT application_id, action_taken, action_taken_name FROM preliminary;

INSERT INTO msamd (application_id, msamd, msamd_name)
SELECT application_id, msamd, msamd_name FROM preliminary;

INSERT INTO state (application_id, state_code, state_name, state_abbr)
SELECT application_id, state_code, state_name, state_abbr FROM preliminary;

INSERT INTO county (application_id, county_code, county_name)
SELECT application_id, county_code, county_name FROM preliminary;

INSERT INTO applicant_ethnicity (application_id, applicant_ethnicity, applicant_ethnicity_name)
SELECT application_id, applicant_ethnicity, applicant_ethnicity_name FROM preliminary;

INSERT INTO co_applicant_ethnicity (application_id, co_applicant_ethnicity, co_applicant_ethnicity_name)
SELECT application_id, co_applicant_ethnicity, co_applicant_ethnicity_name FROM preliminary;

INSERT INTO applicant_race (application_id, applicant_race_1, applicant_race_name_1, applicant_race_2, applicant_race_name_2, applicant_race_3, applicant_race_name_3, applicant_race_4, applicant_race_name_4, applicant_race_5, applicant_race_name_5)
SELECT application_id, applicant_race_1, applicant_race_name_1, applicant_race_2, applicant_race_name_2, applicant_race_3, applicant_race_name_3, applicant_race_4, applicant_race_name_4, applicant_race_5, applicant_race_name_5 FROM preliminary;

INSERT INTO co_applicant_race (application_id, co_applicant_race_1, co_applicant_race_name_1, co_applicant_race_2, co_applicant_race_name_2, co_applicant_race_3, co_applicant_race_name_3, co_applicant_race_4, co_applicant_race_name_4, co_applicant_race_5, co_applicant_race_name_5)
SELECT application_id, co_applicant_race_1, co_applicant_race_name_1, co_applicant_race_2, co_applicant_race_name_2, co_applicant_race_3, co_applicant_race_name_3, co_applicant_race_4, co_applicant_race_name_4, co_applicant_race_5, co_applicant_race_name_5 FROM preliminary;

INSERT INTO applicant_sex (application_id, applicant_sex, applicant_sex_name)
SELECT application_id, applicant_sex, applicant_sex_name FROM preliminary;

INSERT INTO co_applicant_sex (application_id, co_applicant_sex, co_applicant_sex_name)
SELECT application_id, co_applicant_sex, co_applicant_sex_name FROM preliminary;

INSERT INTO purchaser_type (application_id, purchaser_type, purchaser_type_name)
SELECT application_id, purchaser_type, purchaser_type_name FROM preliminary;

INSERT INTO denial_reason (application_id, denial_reason_1, denial_reason_name_1, denial_reason_2, denial_reason_name_2, denial_reason_3, denial_reason_name_3)
SELECT application_id, denial_reason_1, denial_reason_name_1, denial_reason_2, denial_reason_name_2, denial_reason_3, denial_reason_name_3 FROM preliminary;

INSERT INTO hoepa_status (application_id, hoepa_status, hoepa_status_name)
SELECT application_id, hoepa_status, hoepa_status_name FROM preliminary;

INSERT INTO lien_status (application_id, lien_status, lien_status_name)
SELECT application_id, lien_status, lien_status_name FROM preliminary;

INSERT INTO empty_table (application_id, edit_status, edit_status_name, application_date_indicator, sequence_number)
SELECT application_id, edit_status, edit_status_name, application_date_indicator, sequence_number FROM preliminary;

-- Create location table
CREATE TABLE location (
    location_id SERIAL PRIMARY KEY,
    application_id INTEGER REFERENCES preliminary(application_id),
    county_code VARCHAR(2),
    msamd VARCHAR(20),
    state_code VARCHAR(2),
    census_tract_number VARCHAR(20),
    population VARCHAR(20),
    minority_population VARCHAR(20),
    hud_median_family_income VARCHAR(20),
    tract_to_msamd_income VARCHAR(20),
    number_of_owner_occupied_units VARCHAR(20),
    number_of_1_to_4_family_units VARCHAR(20)
);

-- Insert unique locations into location table
INSERT INTO location (application_id, county_code, msamd, state_code, census_tract_number, population, minority_population, hud_median_family_income, tract_to_msamd_income, number_of_owner_occupied_units, number_of_1_to_4_family_units)
SELECT application_id, county_code, msamd, state_code, census_tract_number, population, minority_population, hud_median_family_income, tract_to_msamd_income, number_of_owner_occupied_units, number_of_1_to_4_family_units
FROM preliminary;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS final_table CASCADE;

-- Create the final table with altered purchaser_type column
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
    purchaser_type VARCHAR(50), -- Changed to VARCHAR(50)
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
