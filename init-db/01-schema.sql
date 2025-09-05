-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS wallet_db;

-- Use the database
USE wallet_db;
-- Create wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at DATE NOT NULL
);

-- Create ledger_transactions table
CREATE TABLE IF NOT EXISTS ledger_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    wallet_id VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2),
    transaction_request_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    transaction_date DATE NOT NULL,
    INDEX idx_ledger_transaction_date (transaction_date));

START TRANSACTION;
-- Insert sample wallet data
INSERT INTO wallets (wallet_id, balance, created_at) VALUES
("WLT-001",1500.75, '2025-09-04'),
("WLT-002",2500.00, '2025-09-04'),
( "WLT-003",750.25, '2025-09-04'),
( "WLT-004",0.00, '2025-09-04'),
( "WLT-005",10000.50, '2025-09-04'),
( "WLT-006",0.50, '2025-09-04'),
( "WLT-007",0.00,  '2025-09-04');

-- Insert sample ledger transactions
INSERT INTO ledger_transactions (wallet_id, amount, transaction_request_id, transaction_id, description, transaction_date) VALUES
("WLT-003", 45.00, 'req-KYC-003', 'ref-KYC-003', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-001", 100.00, 'req-MP-001',   'ref-MP-001', 'Wallet top-up with Mpesa', '2025-09-04'),
("WLT-002", 50.00, 'req-CRB-001', 'ref-CRB-001', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-005", 120.00, 'req-KYC-005', 'ref-KYC-005', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-001", 75.00,  'req-MP-002',   'ref-MP-002', 'Wallet top-up with Mpesa', '2025-09-04'),
("WLT-004", 90.00, 'req-KYC-004', 'ref-KYC-004', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-002", 200.00, 'req-MP-003',   'ref-MP-003', 'Wallet top-up with Mpesa', '2025-09-04'),
("WLT-005", 500.00, 'req-MP-005',   'ref-MP-005', 'Wallet top-up with Mpesa', '2025-09-04'),
("WLT-002", 50.00, 'req-CRB-002', 'ref-CRB-002', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-001", 20.00, 'req-CRB-004', 'ref-CRB-004', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-004", 25.00, 'req-CRB-005', 'ref-CRB-005', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-001", 60.00, 'req-KYC-001', 'ref-KYC-001', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-005", 15.00, 'req-CRB-006', 'ref-CRB-006', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-002", 80.00, 'req-KYC-002', 'ref-KYC-002', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-003", 70.00, 'req-CS-003',  'ref-CS-003', 'Consuming for CREDIT-SCORE', '2025-09-04'),
("WLT-001", 90.00, 'req-CS-001',  'ref-CS-001', 'Consuming for CREDIT-SCORE', '2025-09-04'),
("WLT-004", 85.00, 'req-CS-004',  'ref-CS-004', 'Consuming for CREDIT-SCORE', '2025-09-04'),
("WLT-001", 25.00, 'req-KYC-006', 'ref-KYC-006', 'Consuming for KYC-VERIFICATION', '2025-09-04'),
("WLT-005", 150.00, 'req-CS-005',  'ref-CS-005', 'Consuming for CREDIT-SCORE', '2025-09-04'),
("WLT-003", 120.00, 'req-CRB-003', 'ref-CRB-003', 'Consuming for CRB-CHECK', '2025-09-04'),
("WLT-002", 110.00, 'req-CS-002',  'ref-CS-002', 'Consuming for CREDIT-SCORE', '2025-09-04'),
("WLT-001", 20.00, 'req-CS-006',  'ref-CS-006', 'Consuming for CREDIT-SCORE', '2025-09-04');

COMMIT;