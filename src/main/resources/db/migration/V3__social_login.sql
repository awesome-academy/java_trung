-- ====================================================
-- V3: Social Login Support
-- ====================================================

-- 1. Make password nullable (social-only users have no password)
ALTER TABLE users
    MODIFY COLUMN password VARCHAR(255) NULL COMMENT 'BCrypt hashed; NULL for social-only accounts';

-- 2. Add auth_provider column
ALTER TABLE users
    ADD COLUMN auth_provider ENUM('LOCAL', 'GOOGLE', 'BOTH')
        NOT NULL DEFAULT 'LOCAL'
        COMMENT 'Authentication method'
    AFTER password;

-- 3. Backfill all existing users as LOCAL
UPDATE users SET auth_provider = 'LOCAL';

-- 4. Create user_social_accounts table
CREATE TABLE user_social_accounts (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          CHAR(36)     NOT NULL              COMMENT 'FK → users.id',
    provider_name    VARCHAR(30)  NOT NULL              COMMENT 'e.g. GOOGLE, FACEBOOK, APPLE',
    provider_user_id VARCHAR(255) NOT NULL              COMMENT 'Sub / UID from the OAuth provider',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    -- One social identity can only belong to one user account
    UNIQUE KEY uq_social_provider_id (provider_name, provider_user_id),
    -- One user can link at most one account per provider
    UNIQUE KEY uq_user_provider      (user_id, provider_name),
    CONSTRAINT fk_user_social_accounts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='OAuth / Social accounts linked to a user';

-- 5. Covering index for lookups by user
CREATE INDEX idx_user_social_accounts_user_id ON user_social_accounts (user_id);
