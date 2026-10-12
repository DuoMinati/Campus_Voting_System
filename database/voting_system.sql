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
-- Added by the admin before the election starts (10 Official Candidates)
-- ------------------------------------------------------------
CREATE TABLE candidates (
    candidate_id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_name VARCHAR(100) NOT NULL,
    party_name VARCHAR(100) NOT NULL,
    symbol VARCHAR(50),
    manifesto TEXT,
    total_votes INT NOT NULL DEFAULT 0
);

INSERT INTO candidates (candidate_id, candidate_name, party_name, symbol, manifesto, total_votes) VALUES
(1, 'Rahul Sharma', 'Student Democratic Front', '🦅', 'Advocating for 24/7 digital lab access, upgraded library facilities, and transparent semester project evaluations.', 0),
(2, 'Anita Verma', 'Youth Innovation Alliance', '💡', 'Focused on national hackathon sponsorships, industry internships, and tech club infrastructure funding.', 0),
(3, 'Suresh Iyer', 'Campus United Voice', '🌟', 'Committed to hostel amenities modernization, student welfare grievance committee, and sports festival expansion.', 0),
(4, 'Priya Nair', 'Creative Arts & Media Forum', '🎨', 'Dedicated to annual cultural fest revitalization, campus digital media studio, and creative arts clubs funding.', 0),
(5, 'Rohan Deshmukh', 'Engineering & Tech Syndicate', '⚙️', 'Pushing for advanced robotics labs, cloud computing credits for students, and open-source project grants.', 0),
(6, 'Sneha Patel', 'Eco-Green Campus Movement', '🌱', 'Advancing campus sustainability, solar energy on hostel roofs, waste recycling, and paperless administrative workflows.', 0),
(7, 'Mohammed Zeeshan', 'Sports & Health League', '⚽', 'Upgrading sports complex amenities, inter-college athletic tournaments, and medical insurance coverage for student athletes.', 0),
(8, 'Ananya Roy', 'Women in STEM & Equality Council', '🚀', 'Promoting mentorship programs, diversity scholarships, female leadership seminars, and safe campus transport.', 0),
(9, 'Tanmay Bhatia', 'Entrepreneurship & Incubation Guild', '📈', 'Connecting students with venture capital seed funding, startup incubator facilities, and business pitch competitions.', 0),
(10, 'Divya Menon', 'Academic Excellence & Research Collective', '📚', 'Expanding peer tutoring networks, research publication fee waivers, and international conference travel stipends.', 0);

-- Default Sample Registered Voters (All with 0 votes initially)
INSERT INTO voters (voter_id, full_name, voter_card_no, email, password, dob, age, gender, has_voted) VALUES
(1, 'Vikram Malhotra', '101001', 'vikram@campus.edu', 'voter123', '14-08-2001', 24, 'Male', FALSE),
(2, 'Pooja Deshmukh', '101002', 'pooja@campus.edu', 'voter123', '22-03-2002', 24, 'Female', FALSE),
(3, 'Aakash Banerjee', '101003', 'aakash@campus.edu', 'voter123', '05-11-2001', 24, 'Male', FALSE),
(4, 'Neha Sharma', '101004', 'neha@campus.edu', 'voter123', '19-01-2002', 24, 'Female', FALSE),
(5, 'Jyotirmoy', '101005', 'jyotirmoy@campus.edu', 'voter123', '10-07-2001', 25, 'Male', FALSE);

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
