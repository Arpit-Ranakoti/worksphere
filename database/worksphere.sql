-- WorkSphere database schema
-- Target: MySQL 8+
-- The Spring Boot demo defaults to H2 and Hibernate auto-creates its schema.
-- Use this file when deploying WorkSphere with MySQL.

CREATE DATABASE IF NOT EXISTS worksphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE worksphere;

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE skills (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_skills_name (name)
) ENGINE=InnoDB;

CREATE TABLE worker_profiles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  location VARCHAR(255),
  bio VARCHAR(255),
  rating DOUBLE,
  rating_count INT,
  strike_count INT,
  availability VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_worker_profile_user (user_id),
  CONSTRAINT fk_worker_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE employer_profiles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  company_name VARCHAR(255),
  location VARCHAR(255),
  bio VARCHAR(255),
  rating DOUBLE,
  rating_count INT,
  PRIMARY KEY (id),
  KEY idx_employer_profile_user (user_id),
  CONSTRAINT fk_employer_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE jobs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employer_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  category VARCHAR(255),
  description VARCHAR(4000),
  location VARCHAR(255),
  job_date DATE,
  start_time TIME(6),
  end_time TIME(6),
  pay DOUBLE,
  workers_required INT,
  workers_selected INT,
  deposit_required BOOLEAN NOT NULL DEFAULT FALSE,
  deposit_amount DOUBLE,
  status VARCHAR(32),
  created_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_jobs_employer (employer_id),
  KEY idx_jobs_status (status),
  CONSTRAINT fk_jobs_employer FOREIGN KEY (employer_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE worker_skills (
  id BIGINT NOT NULL AUTO_INCREMENT,
  worker_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_worker_skill (worker_id, skill_id),
  KEY idx_worker_skills_skill (skill_id),
  CONSTRAINT fk_worker_skills_worker FOREIGN KEY (worker_id) REFERENCES worker_profiles(id),
  CONSTRAINT fk_worker_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
) ENGINE=InnoDB;

CREATE TABLE job_skills (
  id BIGINT NOT NULL AUTO_INCREMENT,
  job_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_job_skill (job_id, skill_id),
  KEY idx_job_skills_skill (skill_id),
  CONSTRAINT fk_job_skills_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_job_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
) ENGINE=InnoDB;

CREATE TABLE applications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  job_id BIGINT NOT NULL,
  worker_id BIGINT NOT NULL,
  cover_message VARCHAR(2000),
  status VARCHAR(32),
  applied_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_application_job_worker (job_id, worker_id),
  KEY idx_applications_worker (worker_id),
  CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_applications_worker FOREIGN KEY (worker_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE commitments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  job_id BIGINT NOT NULL,
  worker_id BIGINT NOT NULL,
  employer_id BIGINT NOT NULL,
  application_id BIGINT,
  status VARCHAR(40),
  deposit_status VARCHAR(32),
  cancel_reason VARCHAR(255),
  confirmed_at DATETIME(6),
  completed_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_commitments_job (job_id),
  KEY idx_commitments_worker (worker_id),
  KEY idx_commitments_employer (employer_id),
  KEY idx_commitments_application (application_id),
  CONSTRAINT fk_commitments_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_commitments_worker FOREIGN KEY (worker_id) REFERENCES users(id),
  CONSTRAINT fk_commitments_employer FOREIGN KEY (employer_id) REFERENCES users(id),
  CONSTRAINT fk_commitments_application FOREIGN KEY (application_id) REFERENCES applications(id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(32),
  message VARCHAR(1000),
  read_flag BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_notifications_user (user_id),
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE ratings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  commitment_id BIGINT NOT NULL,
  from_user_id BIGINT NOT NULL,
  to_user_id BIGINT NOT NULL,
  rating INT,
  review VARCHAR(2000),
  created_at DATETIME(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_rating_commitment_from_to (commitment_id, from_user_id, to_user_id),
  KEY idx_ratings_to_user (to_user_id),
  CONSTRAINT fk_ratings_commitment FOREIGN KEY (commitment_id) REFERENCES commitments(id),
  CONSTRAINT fk_ratings_from_user FOREIGN KEY (from_user_id) REFERENCES users(id),
  CONSTRAINT fk_ratings_to_user FOREIGN KEY (to_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE strikes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  status VARCHAR(32),
  reason VARCHAR(255),
  created_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_strikes_user (user_id),
  CONSTRAINT fk_strikes_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE disputes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  commitment_id BIGINT NOT NULL,
  raised_by_id BIGINT NOT NULL,
  reason VARCHAR(255),
  description VARCHAR(4000),
  status VARCHAR(32),
  resolution_note VARCHAR(4000),
  created_at DATETIME(6),
  resolved_at DATETIME(6),
  PRIMARY KEY (id),
  KEY idx_disputes_commitment (commitment_id),
  KEY idx_disputes_raised_by (raised_by_id),
  KEY idx_disputes_status (status),
  CONSTRAINT fk_disputes_commitment FOREIGN KEY (commitment_id) REFERENCES commitments(id),
  CONSTRAINT fk_disputes_raised_by FOREIGN KEY (raised_by_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- Useful starter skills. Demo users/jobs are created by DataInitializer.java in demo mode.
INSERT INTO skills (name) VALUES
  ('Java'), ('Spring Boot'), ('JavaScript'), ('HTML/CSS'), ('React'),
  ('Customer Service'), ('Data Entry'), ('Event Support'), ('Marketing'), ('Excel')
ON DUPLICATE KEY UPDATE name = VALUES(name);
