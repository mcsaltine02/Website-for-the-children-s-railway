-- =============================================
-- Схема для H2 в режиме PostgreSQL
-- =============================================

DROP TABLE IF EXISTS program_employees CASCADE;
DROP TABLE IF EXISTS taught_program CASCADE;
DROP TABLE IF EXISTS professional_retraining CASCADE;
DROP TABLE IF EXISTS professional_development CASCADE;
DROP TABLE IF EXISTS employees_education_level CASCADE;
DROP TABLE IF EXISTS education_level CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS academic_degree CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS attraction_condition CASCADE;

-- Учёная степень
CREATE TABLE if not exists  academic_degree
(
    ad_id    BIGSERIAL PRIMARY KEY,
    academic TEXT
);

-- Должность
CREATE TABLE if not exists  post
(
    p_id BIGSERIAL PRIMARY KEY,
    post TEXT
);

-- Условие привлечения
CREATE TABLE if not exists  attraction_condition
(
    ac_id     BIGSERIAL PRIMARY KEY,
    condition TEXT
);

-- Уровень образования
CREATE TABLE if not exists  education_level
(
    el_id     BIGSERIAL PRIMARY KEY,
    education TEXT
);

-- Сотрудники
CREATE TABLE if not exists  employees
(
    e_id                                       BIGSERIAL PRIMARY KEY,
    first_name                                 VARCHAR(128),
    last_name                                  VARCHAR(128),
    middle_name                                VARCHAR(128),
    work_experience                            INT,
    work_experience_in_educational_institution INT,
    ad_id                                      INTEGER REFERENCES academic_degree(ad_id) ON DELETE SET NULL,
    p_id                                       INTEGER REFERENCES post(p_id) ON DELETE SET NULL,
    ac_id                                      INTEGER REFERENCES attraction_condition(ac_id) ON DELETE SET NULL,
    photo_path                                 VARCHAR(1012) DEFAULT 'no-photo.png'
);

-- Связующая таблица: сотрудник — уровень образования
CREATE TABLE if not exists employees_education_level
(
    eel_id BIGSERIAL PRIMARY KEY,
    e_id   INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    el_id  INTEGER REFERENCES education_level(el_id) ON DELETE CASCADE,
    CONSTRAINT unique_employee_education UNIQUE (e_id, el_id)
);

-- Повышение квалификации
CREATE TABLE if not exists  professional_development
(
    pd_id          BIGSERIAL PRIMARY KEY,
    e_id           INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    qualifications TEXT
);

-- Профессиональная переподготовка
CREATE TABLE if not exists  professional_retraining
(
    pr_id          BIGSERIAL PRIMARY KEY,
    e_id           INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    qualifications TEXT
);

-- Преподаваемая программа
CREATE TABLE if not exists  taught_program
(
    tp_id          BIGSERIAL PRIMARY KEY,
    qualifications TEXT,
    vacant_places INT DEFAULT 0
);

-- Связь "Сотрудник — Преподаваемая программа"
CREATE TABLE if not exists  program_employees
(
    pe_id BIGSERIAL PRIMARY KEY,
    e_id  INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    tp_id INTEGER REFERENCES taught_program(tp_id) ON DELETE RESTRICT,
    CONSTRAINT unique_employee_program UNIQUE (e_id, tp_id)
);


-- Индексы
CREATE INDEX IF NOT EXISTS idx_program_employees_eid ON program_employees(e_id);
CREATE INDEX IF NOT EXISTS idx_program_employees_tpid ON program_employees(tp_id);
CREATE INDEX IF NOT EXISTS idx_employees_education_eid ON employees_education_level(e_id);
CREATE INDEX IF NOT EXISTS idx_employees_education_elid ON employees_education_level(el_id);