-- Учёная степень
CREATE TABLE IF NOT EXISTS academic_degree
(
    ad_id    IDENTITY PRIMARY KEY,
    academic TEXT
);

-- Должность
CREATE TABLE IF NOT EXISTS post
(
    p_id IDENTITY PRIMARY KEY,
    post TEXT
);

-- Условие привлечения
CREATE TABLE IF NOT EXISTS attraction_condition
(
    ac_id     IDENTITY PRIMARY KEY,
    condition TEXT
);

-- Сотрудники
CREATE TABLE IF NOT EXISTS employees
(
    e_id                                       IDENTITY PRIMARY KEY,
    first_name                                 VARCHAR(128),
    last_name                                  VARCHAR(128),
    middle_name                                VARCHAR(128),
    work_experience                            INT,
    work_experience_in_educational_institution INT,
    ad_id                                      INTEGER REFERENCES academic_degree(ad_id) ON DELETE SET NULL,
    p_id                                       INTEGER REFERENCES post(p_id) ON DELETE SET NULL,
    ac_id                                      INTEGER REFERENCES attraction_condition(ac_id) ON DELETE SET NULL,
    photo_path                                 VARCHAR(512) DEFAULT 'no-photo.png'
);

-- Уровень образования
CREATE TABLE IF NOT EXISTS education_level
(
    el_id     IDENTITY PRIMARY KEY,
    education TEXT
);

-- Связующая таблица
CREATE TABLE IF NOT EXISTS employees_education_level
(
    eel_id IDENTITY PRIMARY KEY,
    e_id   INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    el_id  INTEGER REFERENCES education_level(el_id) ON DELETE CASCADE,
    CONSTRAINT unique_employee_education UNIQUE (e_id, el_id)
);

-- Повышение квалификации
CREATE TABLE IF NOT EXISTS professional_development
(
    pd_id          IDENTITY PRIMARY KEY,
    e_id           INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    qualifications TEXT
);

-- Профессиональная переподготовка
CREATE TABLE IF NOT EXISTS professional_retraining
(
    pr_id          IDENTITY PRIMARY KEY,
    e_id           INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    qualifications TEXT
);

-- Преподаваемая программа
CREATE TABLE IF NOT EXISTS taught_program
(
    tp_id          IDENTITY PRIMARY KEY,
    qualifications TEXT,
    taught_program INT DEFAULT 0
);

-- Связь "Сотрудник — Преподаваемая программа"
CREATE TABLE IF NOT EXISTS program_employees
(
    pe_id IDENTITY PRIMARY KEY,
    e_id  INTEGER REFERENCES employees(e_id) ON DELETE CASCADE,
    tp_id INTEGER REFERENCES taught_program(tp_id) ON DELETE RESTRICT,
    CONSTRAINT unique_employee_program UNIQUE (e_id, tp_id)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_program_employees_eid ON program_employees(e_id);
CREATE INDEX IF NOT EXISTS idx_program_employees_tpid ON program_employees(tp_id);
CREATE INDEX IF NOT EXISTS idx_employees_education_eid ON employees_education_level(e_id);
CREATE INDEX IF NOT EXISTS idx_employees_education_elid ON employees_education_level(el_id);