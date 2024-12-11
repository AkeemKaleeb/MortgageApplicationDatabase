COPY (
    SELECT 
        f.as_of_year, 
        f.respondent_id, 
        a.agency_name, 
        a.agency_abbr, 
        f.agency_code, 
        lt.loan_type_name, 
        f.loan_type, 
        pt.property_type_name, 
        f.property_type, 
        lp.loan_purpose_name, 
        f.loan_purpose, 
        oo.owner_occupancy_name, 
        f.owner_occupancy, 
        f.loan_amount_000s, 
        p.preapproval_name, 
        f.preapproval, 
        at.action_taken_name, 
        f.action_taken, 
        m.msamd_name, 
        f.msamd, 
        s.state_name, 
        s.state_abbr, 
        f.state_code, 
        c.county_name, 
        f.county_code, 
        l.census_tract_number, 
        ae.applicant_ethnicity_name, 
        f.applicant_ethnicity, 
        cae.co_applicant_ethnicity_name, 
        f.co_applicant_ethnicity, 
        f.applicant_race_1, 
        f.applicant_race_name_1, 
        f.applicant_race_2, 
        f.applicant_race_name_2, 
        f.applicant_race_3, 
        f.applicant_race_name_3, 
        f.applicant_race_4, 
        f.applicant_race_name_4, 
        f.applicant_race_5, 
        f.applicant_race_name_5, 
        f.co_applicant_race_1, 
        f.co_applicant_race_name_1, 
        f.co_applicant_race_2, 
        f.co_applicant_race_name_2, 
        f.co_applicant_race_3, 
        f.co_applicant_race_name_3, 
        f.co_applicant_race_4, 
        f.co_applicant_race_name_4, 
        f.co_applicant_race_5, 
        f.co_applicant_race_name_5, 
        asx.applicant_sex_name, 
        f.applicant_sex, 
        casx.co_applicant_sex_name, 
        f.co_applicant_sex, 
        f.applicant_income_000s, 
        pur.purchaser_type_name, 
        f.purchaser_type, 
        f.rate_spread, 
        hs.hoepa_status_name, 
        f.hoepa_status, 
        ls.lien_status_name, 
        f.lien_status, 
        es.edit_status_name, 
        f.edit_status, 
        f.sequence_number, 
        l.population, 
        l.minority_population, 
        l.hud_median_family_income, 
        l.tract_to_msamd_income, 
        l.number_of_owner_occupied_units, 
        l.number_of_1_to_4_family_units, 
        f.application_date_indicator
    FROM 
        final_table f
    JOIN 
        agency a ON f.application_id = a.application_id
    JOIN 
        loan_type lt ON f.application_id = lt.application_id
    JOIN 
        property_type pt ON f.application_id = pt.application_id
    JOIN 
        loan_purpose lp ON f.application_id = lp.application_id
    JOIN 
        owner_occupancy oo ON f.application_id = oo.application_id
    JOIN 
        preapproval p ON f.application_id = p.application_id
    JOIN 
        action_taken at ON f.application_id = at.application_id
    JOIN 
        msamd m ON f.application_id = m.application_id
    JOIN 
        state s ON f.application_id = s.application_id
    JOIN 
        county c ON f.application_id = c.application_id
    JOIN 
        location l ON f.application_id = l.location_id
    JOIN 
        applicant_ethnicity ae ON f.application_id = ae.application_id
    JOIN 
        co_applicant_ethnicity cae ON f.application_id = cae.application_id
    JOIN 
        applicant_sex asx ON f.application_id = asx.application_id
    JOIN 
        co_applicant_sex casx ON f.application_id = casx.application_id
    JOIN 
        purchaser_type pur ON f.application_id = pur.application_id
    JOIN 
        hoepa_status hs ON f.application_id = hs.application_id
    JOIN 
        lien_status ls ON f.application_id = ls.application_id
    JOIN 
        empty_table es ON f.application_id = es.application_id
) TO '/Users/haris/working_dir/Mortgage-Application-Database/final_table.csv' WITH CSV HEADER;

