-- ==========================================================================
-- COLLEGE LOST & FOUND PORTAL - MYSQL DATABASE SCHEMA
-- Normalized structure designed for 4th Sem Engineering Mini Project
-- Supports JDBC client transactions, Auto-Increment Primary Keys & BLOBs
-- ==========================================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS lost_and_found_db;
USE lost_and_found_db;

-- 2. Drop existing tables if they exist to prevent schema collision errors (Dependencies first)
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS found_items;
DROP TABLE IF EXISTS lost_items;
DROP TABLE IF EXISTS users;

-- 3. Users Table (Authentication & Session levels)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) UNIQUE NOT NULL, -- Used for login (e.g. Student USN or Admin Username)
    name VARCHAR(100) NOT NULL,
    password VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'student' -- 'student' or 'admin'
) ENGINE=InnoDB;

-- 4. Lost Items Table (Supports BLOBs for image uploading)
CREATE TABLE lost_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    usn VARCHAR(15) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    image_blob LONGBLOB, -- Stores binary files directly in DB
    image_path VARCHAR(255), -- Stores the uploaded file path
    date_lost DATE NOT NULL,
    location_lost VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending', -- 'Pending', 'Claimed', 'Under Verification', 'Ready for Collection', 'Returned'
    deposited_to VARCHAR(50) DEFAULT 'College Office', -- 'Security Room', 'College Office', 'Lab Incharge'
    returned_by_admin VARCHAR(50),
    return_date DATE,
    verification_notes TEXT
) ENGINE=InnoDB;

-- 5. Found Items Table (Safe custody tracking points)
CREATE TABLE found_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    date_found DATE NOT NULL,
    location_found VARCHAR(100) NOT NULL,
    safe_custody VARCHAR(100) NOT NULL, -- Location where item is kept safely (e.g. CSE Office)
    reporter_name VARCHAR(100),
    contact VARCHAR(15),
    status VARCHAR(20) DEFAULT 'Found' -- 'Found', 'Returned'
) ENGINE=InnoDB;

-- 6. Claim Items & Owner Verification Table
CREATE TABLE claims (
    claim_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    usn VARCHAR(15) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    proof TEXT NOT NULL,
    claim_status VARCHAR(20) DEFAULT 'Pending', -- 'Pending', 'Under Verification', 'Approved', 'Rejected', 'Returned'
    claim_date DATE NOT NULL,
    uploaded_proof LONGBLOB,
    FOREIGN KEY (item_id) REFERENCES lost_items(id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ==========================================================================
-- INSERT STATIC SAMPLE DATA SYNCHRONOUS WITH PRESENTATION SLIDES
-- ==========================================================================

-- Insert Users (Student & Admin profiles)
INSERT INTO users (username, name, password, department, role) VALUES 
('1RV24CS015', 'Aishu R. Kumar', 'password123', 'Computer Science', 'student'),
('1RV23CS084', 'Rohan Sharma', 'rohan84', 'Computer Science', 'student'),
('1RV23IS120', 'Priya Patel', 'priya120', 'Information Science', 'student'),
('admin', 'System Administrator', 'admin123', 'Office Operations', 'admin');

-- Insert Lost Items
INSERT INTO lost_items (student_name, usn, contact, department, title, image_blob, date_lost, location_lost, status) VALUES
('Rohan Sharma', '1RV23CS084', '9845012345', 'Computer Science', 'Black leather Laptop Bag', NULL, '2026-05-25', 'Main Block Seminar Hall', 'Pending'),
('Siddharth M.', '1RV23ME098', '9880123456', 'Mechanical Engineering', 'Honda Motorcycle Keys', NULL, '2026-05-26', 'Mechanical Labs parking', 'Pending'),
('Sneha Rao', '1RV23CS140', '9448123456', 'Computer Science', 'MacBook Air M2 Silver', NULL, '2026-05-27', 'Computer Lab 3 Desk 12', 'Pending'),
('Amit Hegde', '1RV23EC015', '9611123456', 'Electronics & Comm', 'Engineering College ID Card', NULL, '2026-05-24', 'Library Reading Room A', 'Returned');

-- Insert Found Items
INSERT INTO found_items (title, date_found, location_found, safe_custody, reporter_name, contact, status) VALUES
('Redmi Note 12 Pro (Blue)', '2026-05-26', 'College Cafeteria Table 4', 'Physical Education Room (Sports Dept)', 'Pooja R', '9008123456', 'Found'),
('Casio Scientific Calculator', '2026-05-27', 'Chemistry Lab Table 2', 'Chemistry Lab Office', 'Karthik S', '9113123456', 'Found');

-- Insert Claims matching slide operations
INSERT INTO claims (item_id, student_name, usn, contact, department, proof, claim_status, claim_date) VALUES
(1, 'Rohan Sharma', '1RV23CS084', '9845012345', 'Computer Science', 'It is a black leather bag with my name inside', 'Pending', '2026-05-26'),
(4, 'Amit Hegde', '1RV23EC015', '9611123456', 'Electronics & Comm', 'I lost it in the library reading room', 'Approved', '2026-05-25');

-- Commit records
COMMIT;
