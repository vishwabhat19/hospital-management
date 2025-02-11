CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255),
    parent_group_id VARCHAR(255),
    group_name VARCHAR(255),
    created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_activity_log (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    parent_group_id VARCHAR(255),
    action_type VARCHAR(10),
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dlq_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_content TEXT,
    timestamp TIMESTAMP
);