CREATE TABLE Parameters(
    Parameter_id VARCHAR(255) PRIMARY KEY UNIQUE,
    Profile_name VARCHAR(255) UNIQUE,
    P_value      VARCHAR(255),
    q_value      VARCHAR(255),
    f_value      VARCHAR(255),
    h_value      VARCHAR(255),
    k_value      VARCHAR(255)
);
CREATE TABLE Cohorts(
    Cohort_id       VARCHAR(255) PRIMARY KEY,
    Country_code    VARCHAR(255)
);
CREATE TABLE Health_data(
    Report_id      VARCHAR(255) PRIMARY KEY,
    Device_id      VARCHAR(255),
    Cohort         VARCHAR(255) REFERENCES Cohorts,
    Interval_start VARCHAR(255),
    Interval_end   VARCHAR(255),
    Irr            VARCHAR(255),
    Prr            VARCHAR(255),
    Step_count     VARCHAR(255),
    Parameter_id   VARCHAR(255) REFERENCES Parameters
);

