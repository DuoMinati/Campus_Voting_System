-- ============================================================
--  Online Voting System - Database Script
--  MCA Project
--
--  Run this whole file in MySQL Workbench (or mysql command line)
--  before running the Java project. It will create the database,
--  all tables, and drop a few sample rows in so the project is
--  not empty the first time you run it.
-- ============================================================

DROP DATABASE IF EXISTS voting_system;
CREATE DATABASE voting_system;
USE voting_system;

-- ------------------------------------------------------------
-- 1. ADMIN TABLE
-- Only the admin can add candidates / open / close the election
-- ------------------------------------------------------------
CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

-- default admin login -> username: admin , password: admin123
INSERT INTO admin (username, password) VALUES ('admin', 'admin123');

-- ------------------------------------------------------------
-- 2. VOTERS TABLE
-- Every citizen/student who wants to vote must register here first
-- ------------------------------------------------------------
CREATE TABLE voters (
    voter_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    voter_card_no VARCHAR(20) NOT NULL UNIQUE,   -- like a voter id card number
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    dob VARCHAR(20),
    age INT NOT NULL,
    gender VARCHAR(10),
    has_voted BOOLEAN NOT NULL DEFAULT FALSE,
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. CANDIDATES TABLE
-- Added by the admin before the election starts
-- ------------------------------------------------------------
CREATE TABLE candidates (
    candidate_id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_name VARCHAR(100) NOT NULL,
    party_name VARCHAR(100) NOT NULL,
    symbol VARCHAR(50),
    total_votes INT NOT NULL DEFAULT 0
);

INSERT INTO candidates (candidate_name, party_name, symbol) VALUES
('Rahul Sharma', 'Progress Party', 'Lotus'),
('Anita Verma', 'Unity Front', 'Umbrella'),
('Suresh Iyer', 'Peoples Alliance', 'Star');

-- ------------------------------------------------------------
-- 4. VOTES TABLE
-- One row is created every time a vote is cast.
-- voter_id has a UNIQUE constraint so that the same voter
-- can never be inserted twice -> stops double voting at the
-- database level itself (extra safety along with has_voted flag)
-- ------------------------------------------------------------
CREATE TABLE votes (
    vote_id INT AUTO_INCREMENT PRIMARY KEY,
    voter_id INT NOT NULL UNIQUE,
    candidate_id INT NOT NULL,
    voted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voter_id) REFERENCES voters(voter_id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id)
);

-- ------------------------------------------------------------
-- 5. ELECTION_STATUS TABLE
-- Simple single row table the admin uses to open/close voting
-- ------------------------------------------------------------
CREATE TABLE election_status (
    id INT PRIMARY KEY DEFAULT 1,
    election_title VARCHAR(150) DEFAULT 'MCA College Election 2026',
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO election_status (id, election_title, is_active) VALUES (1, 'MCA College Election 2026', TRUE);

-- ------------------------------------------------------------
-- quick check
-- ------------------------------------------------------------
SELECT * FROM admin;
SELECT * FROM candidates;
SELECT * FROM election_status;
