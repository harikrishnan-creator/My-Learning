-- Initialize database for Liquibase
-- This file runs automatically when MySQL container starts

-- Grant privileges to liquibase_user
GRANT ALL PRIVILEGES ON liquibase_db.* TO 'liquibase_user'@'%';
FLUSH PRIVILEGES;

-- Verify database exists
USE liquibase_db;

-- Display creation confirmation
SELECT 'Database liquibase_db initialized successfully' AS status;
