-- =============================================
-- Заполнение справочников
-- =============================================

-- Учёные степени / категории
INSERT INTO academic_degree (academic) VALUES
                                           ('без категории'),
                                           ('1 квалификационная категория'),
                                           ('кандидат технических наук');

-- Должности
INSERT INTO post (post) VALUES
                            ('Педагог дополнительного образования'),
                            ('Инструктор детских железных дорог РДЖД'),
                            ('Педагог-психолог'),
                            ('Методист');

-- Условия привлечения
INSERT INTO attraction_condition (condition) VALUES
                                                 ('по основному месту работы'),
                                                 ('совместительство');

-- Уровни образования
INSERT INTO education_level (education) VALUES
                                            ('Среднее профессиональное образование'),
                                            ('Высшее'),
                                            ('Высшее образование');

-- Преподаваемые программы
INSERT INTO taught_program (qualifications) VALUES
                                                ('Введение в железнодорожную отрасль'),
                                                ('Общий курс железных дорог'),
                                                ('Вагоны и вагонное хозяйство'),
                                                ('Основы проектной деятельности в области ж/д транспорта'),
                                                ('Путь и путевое хозяйство'),
                                                ('Основы транспортной логистики'),
                                                ('Основы моделирования и макетирования'),
                                                ('Организация перевозок и управление на транспорте (ДСП)'),
                                                ('Организация перевозок и управление на транспорте (поездной диспетчер)'),
                                                ('Локомотивы и локомотивное хозяйство (ПМТ)'),
                                                ('Локомотивы и локомотивное хозяйство (МТ)'),
                                                ('Автоматика, телемеханика и связь');

-- =============================================
-- Сотрудники
-- =============================================

-- 1. Боженская Олеся Георгиевна
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution, photo_path)
VALUES ('Боженская', 'Олеся', 'Георгиевна',
        (SELECT p_id FROM post WHERE post = 'Педагог дополнительного образования'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = 'без категории'),
        3, 1, 'no-photo.png');

-- 2. Иваненко Дмитрий Алексеевич
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Иваненко', 'Дмитрий', 'Алексеевич',
        (SELECT p_id FROM post WHERE post = 'Педагог дополнительного образования'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = '1 квалификационная категория'),
        3, 2);

-- 3. Королев Виктор Николаевич
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Королев', 'Виктор', 'Николаевич',
        (SELECT p_id FROM post WHERE post = 'Педагог дополнительного образования'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = '1 квалификационная категория'),
        26, 8);

-- 4. Нечаева Елена Александровна
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Нечаева', 'Елена', 'Александровна',
        (SELECT p_id FROM post WHERE post = 'Педагог дополнительного образования'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = '1 квалификационная категория'),
        32, 9);

-- 5. Полтавский Сергей Николаевич
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Полтавский', 'Сергей', 'Николаевич',
        (SELECT p_id FROM post WHERE post = 'Педагог дополнительного образования'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = 'без категории'),
        40, 1);

-- 6. Гапеев Игорь Николаевич
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Гапеев', 'Игорь', 'Николаевич',
        (SELECT p_id FROM post WHERE post = 'Инструктор детских железных дорог РДЖД'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'совместительство'),
        (SELECT ad_id FROM academic_degree WHERE academic = 'без категории'),
        7, 1);

-- 7. Шилова Анастасия Михайловна
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Шилова', 'Анастасия', 'Михайловна',
        (SELECT p_id FROM post WHERE post = 'Педагог-психолог'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'по основному месту работы'),
        (SELECT ad_id FROM academic_degree WHERE academic = 'без категории'),
        1, 0);

-- 8. Гапеева Татьяна Владимировна
INSERT INTO employees (last_name, first_name, middle_name, p_id, ac_id, ad_id,
                       work_experience, work_experience_in_educational_institution)
VALUES ('Гапеева', 'Татьяна', 'Владимировна',
        (SELECT p_id FROM post WHERE post = 'Методист'),
        (SELECT ac_id FROM attraction_condition WHERE condition = 'совместительство'),
        (SELECT ad_id FROM academic_degree WHERE academic = 'кандидат технических наук'),
        25, 2);

-- =============================================
-- Связь "Сотрудник — Уровень образования"
-- =============================================

INSERT INTO employees_education_level (e_id, el_id)
SELECT e.e_id, el.el_id
FROM employees e
         CROSS JOIN education_level el
WHERE (e.last_name = 'Боженская' AND el.education LIKE '%Среднее%')
   OR (e.last_name = 'Иваненко' AND el.education LIKE '%Высшее%')
   OR (e.last_name = 'Королев' AND el.education LIKE '%Среднее%')
   OR (e.last_name = 'Нечаева' AND el.education LIKE '%Среднее%')
   OR (e.last_name = 'Нечаева' AND el.education LIKE '%Высшее%')
   OR (e.last_name = 'Полтавский' AND el.education LIKE '%Высшее%')
   OR (e.last_name = 'Гапеев' AND el.education LIKE '%Высшее%')
   OR (e.last_name = 'Шилова' AND el.education LIKE '%Высшее%')
   OR (e.last_name = 'Гапеева' AND el.education ILIKE '%высшее%');