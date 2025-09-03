-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS wallet_db;

-- Use the database
USE wallet_db;

-- Create wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_wallet_customer (customer_id)
);

-- Create ledger_transactions table
CREATE TABLE IF NOT EXISTS ledger_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2),
    tran_type VARCHAR(50) NOT NULL,
    transaction_request_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_reference VARCHAR(255) NOT NULL,
    source VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ledger_wallet (wallet_id),
    INDEX idx_ledger_tx_request (transaction_request_id)
 );