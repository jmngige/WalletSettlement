-- Insert sample wallet data
INSERT INTO wallets (customer_id, balance, status, created_at) VALUES
('CUST-001', 1500.75, 'ACTIVE', NOW()),
('CUST-002', 2500.00, 'ACTIVE', NOW()),
('CUST-003', 750.25, 'ACTIVE', NOW()),
('CUST-004', 0.00, 'ACTIVE', NOW()),
('CUST-005', 10000.50, 'ACTIVE', NOW()),
('CUST-006', 0.50, 'ACTIVE', NOW()),
('CUST-007', 0.00, 'INACTIVE', NOW());

-- Insert sample ledger transactions
-- SHUFFLED INSERT SCRIPT

INSERT INTO ledger_transactions (wallet_id, customer_id, amount, tran_type, transaction_request_id, transaction_reference, source, description, transaction_date) VALUES
(3, 'CUST-003', 45.00,  'DEBIT', 'req-KYC-003', 'ref-KYC-003', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(1, 'CUST-001', 100.00, 'CREDIT', 'req-MP-001',   'ref-MP-001',      'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(2, 'CUST-002', 50.00,  'DEBIT', 'req-CRB-001', 'ref-CRB-001', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(5, 'CUST-005', 120.00, 'DEBIT', 'req-KYC-005', 'ref-KYC-005', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(1, 'CUST-001', 75.00,  'CREDIT', 'req-MP-002',   'ref-MP-002',      'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(4, 'CUST-004', 90.00,  'DEBIT', 'req-KYC-004', 'ref-KYC-004', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(2, 'CUST-002', 200.00, 'CREDIT', 'req-MP-003',   'ref-MP-003',      'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(5, 'CUST-005', 500.00, 'CREDIT', 'req-MP-005',   'ref-MP-005',      'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(2, 'CUST-002', 200.00, 'CREDIT', 'req-MP-004',  'ref-MP-003',  'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(3, 'CUST-003', 300.00, 'CREDIT', 'req-MP-005',   'ref-MP-004',      'EXTERNAL', 'Wallet top-up with Mpesa', NOW()),
(2, 'CUST-002', 50.00,  'DEBIT', 'req-CRB-002', 'ref-CRB-002', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(1, 'CUST-001', 20.00,  'DEBIT', 'req-CRB-004', 'ref-CRB-004', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(4, 'CUST-004', 25.00,  'DEBIT', 'req-CRB-005', 'ref-CRB-005', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(1, 'CUST-001', 60.00,  'DEBIT', 'req-KYC-001', 'ref-KYC-001', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(5, 'CUST-005', 15.00,  'DEBIT', 'req-CRB-006', 'ref-CRB-006', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(2, 'CUST-002', 80.00,  'DEBIT', 'req-KYC-002', 'ref-KYC-002', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(3, 'CUST-003', 70.00,  'DEBIT', 'req-CS-003',  'ref-CS-003', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW()),
(1, 'CUST-001', 90.00,  'DEBIT', 'req-CS-001',  'ref-CS-001', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW()),
(4, 'CUST-004', 85.00,  'DEBIT', 'req-CS-004',  'ref-CS-004', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW()),
(1, 'CUST-001', 25.00,  'DEBIT', 'req-KYC-006', 'ref-KYC-006', 'INTERNAL', 'Consuming for KYC-VERIFICATION', NOW()),
(5, 'CUST-005', 150.00, 'DEBIT', 'req-CS-005',  'ref-CS-005', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW()),
(3, 'CUST-003', 120.00, 'DEBIT', 'req-CRB-003', 'ref-CRB-003', 'INTERNAL', 'Consuming for CRB-CHECK', NOW()),
(2, 'CUST-002', 110.00, 'DEBIT', 'req-CS-002',  'ref-CS-002', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW()),
(1, 'CUST-001', 20.00,  'DEBIT', 'req-CS-006',  'ref-CS-006', 'INTERNAL', 'Consuming for CREDIT-SCORE', NOW());
