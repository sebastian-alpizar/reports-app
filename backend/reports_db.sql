-- =========================
-- TABLE: users
-- =========================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,  -- Cambiado de SERIAL a BIGSERIAL
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    national_id VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- TABLE: reports
-- =========================
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,  -- Cambiado de SERIAL a BIGSERIAL
    description TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    approximate_location VARCHAR(255),
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category VARCHAR(100),
    status VARCHAR(50),
    user_id BIGINT,  -- Cambiado de INTEGER a BIGINT
    
    CONSTRAINT fk_report_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================
-- TABLE: photos
-- =========================
CREATE TABLE photos (
    id BIGSERIAL PRIMARY KEY,  -- Cambiado de SERIAL a BIGSERIAL
    url TEXT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ai_validated BOOLEAN DEFAULT FALSE,
    report_id BIGINT,  -- Cambiado de INTEGER a BIGINT

    CONSTRAINT fk_photo_report
        FOREIGN KEY (report_id)
        REFERENCES reports(id)
        ON DELETE CASCADE
);

-- Índices recomendados
CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_category ON reports(category);
CREATE INDEX idx_photos_report_id ON photos(report_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_national_id ON users(national_id);