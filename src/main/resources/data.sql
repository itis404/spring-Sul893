INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_MODERATOR'), ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('admin', 'admin@admin.com', '$2b$12$xO./ZSVjVLpv/vKlMVniVOyuM5lEcSP2KXm.olVhSrP91U2oICosG', TRUE, NOW())
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);