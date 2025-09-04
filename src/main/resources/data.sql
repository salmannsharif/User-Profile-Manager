-- Insert default admin
INSERT INTO user_profile (name, role, email, password, address)
VALUES (
    'Admin User',
    'ADMIN',
    'admin@example.com',
    '$2a$10$W9jeSKsR1Aqj1ZJ0vGz3AeZkK5Y5XwbAZyQY2s3.t9rv0myc3zMea', -- BCrypt hash for "password"
    'Default Address'
);
