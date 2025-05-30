-- V1__Initial_schema.sql
-- Case Management System Initial Database Schema

-- Create cases table
CREATE TABLE IF NOT EXISTS cases (
    id BIGSERIAL PRIMARY KEY,
    case_id VARCHAR(50) UNIQUE NOT NULL,
    case_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    creator_id VARCHAR(100) NOT NULL,
    assignee_id VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20),
    flowable_process_instance_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    task_id VARCHAR(50) UNIQUE NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    assignee_id VARCHAR(100),
    group_name VARCHAR(100),
    case_id BIGINT NOT NULL,
    flowable_task_id VARCHAR(100),
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
);

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(20) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    description TEXT,
    old_value TEXT,
    new_value TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_cases_case_id ON cases(case_id);
CREATE INDEX IF NOT EXISTS idx_cases_status ON cases(status);
CREATE INDEX IF NOT EXISTS idx_cases_creator_id ON cases(creator_id);
CREATE INDEX IF NOT EXISTS idx_cases_assignee_id ON cases(assignee_id);
CREATE INDEX IF NOT EXISTS idx_cases_case_type ON cases(case_type);

CREATE INDEX IF NOT EXISTS idx_tasks_task_id ON tasks(task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX IF NOT EXISTS idx_tasks_group_name ON tasks(group_name);
CREATE INDEX IF NOT EXISTS idx_tasks_case_id ON tasks(case_id);

CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);

-- Insert initial data for testing
INSERT INTO cases (case_id, case_type, status, creator_id, title, description, priority) 
VALUES 
    ('CASE-SAMPLE01', 'FRAUD', 'DRAFT', 'admin', 'Sample Fraud Case', 'Sample fraud investigation case for testing', 'HIGH'),
    ('CASE-SAMPLE02', 'DISPUTE', 'READY', 'admin', 'Payment Dispute Case', 'Customer payment dispute case', 'MEDIUM')
ON CONFLICT (case_id) DO NOTHING;
