-- ================================================================
--  DevSpark — Child Care Management System
--  File    : V1__initial_schema_full.sql
--  Team    : DevSpark | University of Moratuwa, FIT
--  Rules   : UUID PKs | Soft Deletes | Audit Columns (README §5.1)

--  TABLE CREATION ORDER (Order is important due to FK forward-references)
--    1.  account
--    2.  otp_token
--    3.  admin
--    4.  parent_registration_request
--    5.  parent
--    6.  child
--    7.  child_summary
--    8.  teacher_invitation
--    9.  teacher
--    10. activity
--    11. teacher_activity_assignment
--    12. activity_progress_log
--    13. attendance
--    14. meal_menu
--    15. meal_consumption_log
--    16. payment
--    17. payment_transaction
--    18. card_details
--    19. notification
--    20. notification_target
--    21. push_subscription
--    22. chat_thread
--    23. chat_message
--    24. faq
-- ================================================================


-- ================================================================
-- 1. ACCOUNT
--    Teacher, Parent, Admin login credentials
--    Separated by ADMIN / TEACHER / PARENT via the role column
-- ================================================================
CREATE TABLE account (
                         account_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
                         email           VARCHAR(150)    NOT NULL,
                         password_hash   VARCHAR(255)    NOT NULL,
                         role            ENUM('ADMIN','TEACHER','PARENT') NOT NULL,
                         is_verified     BOOLEAN         NOT NULL DEFAULT FALSE,
                         status          ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',

    -- Audit (README §5.1 — every table)
                         created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         created_by      VARCHAR(128),

    -- Soft delete (README §5.1 — never hard-delete)
                         deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
                         deleted_at      DATETIME        NULL,

                         PRIMARY KEY (account_id),
                         UNIQUE KEY uk_account_email (email)
);


-- ================================================================
-- 2. OTP TOKEN
--    Email / phone verification OTP codes
-- ================================================================
CREATE TABLE otp_token (
                           otp_id      CHAR(36)    NOT NULL DEFAULT (UUID()),
                           account_id  CHAR(36)    NOT NULL,
                           otp_code    VARCHAR(10) NOT NULL,
                           expires_at  DATETIME    NOT NULL,
                           is_used     BOOLEAN     NOT NULL DEFAULT FALSE,

                           created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           created_by  VARCHAR(128),
                           deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                           deleted_at  DATETIME    NULL,

                           PRIMARY KEY (otp_id),
                           CONSTRAINT fk_otp_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);


-- ================================================================
-- 3. ADMIN
--    System administrator profile
-- ================================================================
CREATE TABLE admin (
                       admin_id    CHAR(36)        NOT NULL DEFAULT (UUID()),
                       account_id  CHAR(36)        NOT NULL,
                       full_name   VARCHAR(150)    NOT NULL,

                       created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       created_by  VARCHAR(128),
                       deleted     BOOLEAN         NOT NULL DEFAULT FALSE,
                       deleted_at  DATETIME        NULL,

                       PRIMARY KEY (admin_id),
                       UNIQUE KEY uk_admin_account (account_id),
                       CONSTRAINT fk_admin_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);


-- ================================================================
-- 4. PARENT REGISTRATION REQUEST
--    Parent register request pending until admin approval
--    If Admin approves -> creates records in parent and account tables
-- ================================================================
CREATE TABLE parent_registration_request (
                                             request_id          CHAR(36)        NOT NULL DEFAULT (UUID()),

    -- Parent info
                                             first_name          VARCHAR(100)    NOT NULL,
                                             last_name           VARCHAR(100)    NOT NULL,
                                             nic                 VARCHAR(20)     NOT NULL,
                                             email               VARCHAR(150)    NOT NULL,
                                             phone               VARCHAR(20),
                                             address             TEXT,
                                             relationship        ENUM('MOTHER','FATHER','GUARDIAN') NOT NULL,
                                             password_hash       VARCHAR(255)    NOT NULL,

    -- Child info (submitted by parent in the registration form)
                                             child_first_name    VARCHAR(100),
                                             child_last_name     VARCHAR(100),
                                             child_dob           DATE,
                                             child_gender        ENUM('MALE','FEMALE','OTHER'),
                                             child_blood_group   VARCHAR(5),
                                             child_weight        DECIMAL(5,2),
                                             child_height        DECIMAL(5,2),
                                             child_special_note  TEXT,

    -- Request metadata
                                             category            ENUM('CHILD_INFORMATION','BILLING_QUERY','OTHER'),
                                             message             TEXT,
                                             description         TEXT,
                                             status              ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',

                                             created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             created_by          VARCHAR(128),
                                             deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
                                             deleted_at          DATETIME        NULL,

                                             PRIMARY KEY (request_id)
);


-- ================================================================
-- 5. PARENT
--    Approved parent profile (created after admin approval)
-- ================================================================
CREATE TABLE parent (
                        parent_id       CHAR(36)        NOT NULL DEFAULT (UUID()),
                        account_id      CHAR(36)        NOT NULL,
                        full_name       VARCHAR(150)    NOT NULL,
                        address         TEXT,
                        phone           VARCHAR(20),
                        nic             VARCHAR(20),
                        relationship    ENUM('MOTHER','FATHER','GUARDIAN'),
                        profile_picture VARCHAR(500),   -- Firebase Storage URL

                        created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        created_by      VARCHAR(128),
                        deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
                        deleted_at      DATETIME        NULL,

                        PRIMARY KEY (parent_id),
                        UNIQUE KEY uk_parent_account (account_id),
                        CONSTRAINT fk_parent_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);


-- ================================================================
-- 6. CHILD
--    Child profile — a parent can have multiple children
-- ================================================================
CREATE TABLE child (
                       child_id        CHAR(36)        NOT NULL DEFAULT (UUID()),
                       parent_id       CHAR(36)        NOT NULL,
                       first_name      VARCHAR(100)    NOT NULL,
                       last_name       VARCHAR(100)    NOT NULL,
                       dob             DATE            NOT NULL,
                       gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
                       blood_group     VARCHAR(5),
                       weight          DECIMAL(5,2),   -- kg
                       height          DECIMAL(5,2),   -- cm
                       special_note    TEXT,
                       profile_pic     VARCHAR(500),   -- Firebase Storage URL

                       created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       created_by      VARCHAR(128),
                       deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
                       deleted_at      DATETIME        NULL,

                       PRIMARY KEY (child_id),
                       CONSTRAINT fk_child_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id)
);


-- ================================================================
-- 7. CHILD SUMMARY
--    Saved after Teacher writes a child's progress summary / mood
--    ER diagram: summary_id, days_present, mood, progress_summary,
--                activities_completed_to_date
-- ================================================================
CREATE TABLE child_summary (
                               summary_id                  CHAR(36)    NOT NULL DEFAULT (UUID()),
                               child_id                    CHAR(36)    NOT NULL,
                               summary_date                DATE        NOT NULL,
                               days_present                INT         NOT NULL DEFAULT 0,
                               mood                        ENUM('HAPPY','NEUTRAL','SAD','EXCITED','TIRED') NULL,
                               progress_summary            TEXT,
                               activities_completed_count  INT         NOT NULL DEFAULT 0,

                               created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               created_by  VARCHAR(128),
                               deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                               deleted_at  DATETIME    NULL,

                               PRIMARY KEY (summary_id),
                               CONSTRAINT fk_summary_child FOREIGN KEY (child_id) REFERENCES child (child_id)
);


-- ================================================================
-- 8. TEACHER INVITATION  (Jayamaha)
--    Admin sends invitation token to teacher email
-- ================================================================
CREATE TABLE teacher_invitation (
                                    invitation_id   CHAR(36)        NOT NULL DEFAULT (UUID()),
                                    invitee_email   VARCHAR(150)    NOT NULL,
                                    token           VARCHAR(255)    NOT NULL,
                                    expires_at      DATETIME        NOT NULL,
                                    is_used         BOOLEAN         NOT NULL DEFAULT FALSE,

                                    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    created_by  VARCHAR(128),
                                    deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                    deleted_at  DATETIME    NULL,

                                    PRIMARY KEY (invitation_id),
                                    UNIQUE KEY uk_invitation_token (token)
);


-- ================================================================
-- 9. TEACHER  (Jayamaha)
--    Daycare staff profile
--    designation: SENIOR(max 5 activities) | JUNIOR(max 2 activities)
--    max_daily_activities: admin can change manually (README doc)
-- ================================================================
CREATE TABLE teacher (
                         teacher_id              CHAR(36)        NOT NULL DEFAULT (UUID()),
                         account_id              CHAR(36)        NOT NULL,
                         full_name               VARCHAR(150)    NOT NULL,
                         profile_picture         VARCHAR(500),
                         designation             ENUM('SENIOR','JUNIOR') NOT NULL,
                         max_daily_activities    INT             NOT NULL DEFAULT 2,
    -- If registering a SENIOR, Java code sets default to 5
    -- If registering a JUNIOR, Java code sets default to 2
    -- Admin can also edit this (Manage Teachers page)

                         created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         created_by  VARCHAR(128),
                         deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                         deleted_at  DATETIME    NULL,

                         PRIMARY KEY (teacher_id),
                         UNIQUE KEY uk_teacher_account (account_id),
                         CONSTRAINT fk_teacher_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);


-- ================================================================
-- 10. ACTIVITY  (Wickramasinghe)
--     Activity master list managed (CRUD) by Admin
-- ================================================================
CREATE TABLE activity (
                          activity_id     CHAR(36)        NOT NULL DEFAULT (UUID()),
                          activity_name   VARCHAR(150)    NOT NULL,
                          description     TEXT,
                          required_items  TEXT,           -- JSON array or comma-separated

                          created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          created_by  VARCHAR(128),
                          deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                          deleted_at  DATETIME    NULL,

                          PRIMARY KEY (activity_id)
);


-- ================================================================
-- 11. TEACHER ACTIVITY ASSIGNMENT  (Wickramasinghe)
--     When Admin assigns an activity to a teacher
--     current_load: to check overload warnings (README doc)
--     WARNING trigger: current_load >= teacher.max_daily_activities
-- ================================================================
CREATE TABLE teacher_activity_assignment (
                                             assignment_id   CHAR(36)    NOT NULL DEFAULT (UUID()),
                                             teacher_id      CHAR(36)    NOT NULL,
                                             activity_id     CHAR(36)    NOT NULL,
                                             assigned_date   DATE        NOT NULL,
                                             start_time      TIME        NOT NULL,
                                             end_time        TIME        NOT NULL,
                                             status          ENUM('PENDING','COMPLETED') NOT NULL DEFAULT 'PENDING',
                                             current_load    INT         NOT NULL DEFAULT 0,
    -- Service layer: when assigning a teacher
    --   SELECT COUNT(*) FROM teacher_activity_assignment
    --   WHERE teacher_id = ? AND assigned_date = ? AND status != 'COMPLETED' AND deleted = false
    --   ≥ teacher.max_daily_activities → throw OverloadWarning

                                             created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                             created_by  VARCHAR(128),
                                             deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                             deleted_at  DATETIME    NULL,

                                             PRIMARY KEY (assignment_id),
                                             UNIQUE KEY uk_assignment_teacher_activity_date (teacher_id, activity_id, assigned_date),
                                             CONSTRAINT fk_assignment_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id),
                                             CONSTRAINT fk_assignment_activity FOREIGN KEY (activity_id) REFERENCES activity (activity_id)
);


-- ================================================================
-- 12. ACTIVITY PROGRESS LOG  (Wickramasinghe)
--     When Teacher grades a child's activity performance
--     grading_level: 4 colour buttons (LEVEL_1 lowest → LEVEL_4 highest)
-- ================================================================
CREATE TABLE activity_progress_log (
                                       log_id          CHAR(36)    NOT NULL DEFAULT (UUID()),
                                       assignment_id   CHAR(36)    NOT NULL,
                                       child_id        CHAR(36)    NOT NULL,
                                       grading_level   ENUM('LEVEL_1','LEVEL_2','LEVEL_3','LEVEL_4') NOT NULL,
                                       note            TEXT,       -- optional

                                       created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       created_by  VARCHAR(128),
                                       deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                       deleted_at  DATETIME    NULL,

                                       PRIMARY KEY (log_id),
                                       UNIQUE KEY uk_log_assignment_child (assignment_id, child_id),
                                       CONSTRAINT fk_log_assignment FOREIGN KEY (assignment_id) REFERENCES teacher_activity_assignment (assignment_id),
                                       CONSTRAINT fk_log_child FOREIGN KEY (child_id) REFERENCES child (child_id)
);


-- ================================================================
-- 13. ATTENDANCE  (Wickramasinghe)
--     When Teacher marks daily attendance
--     UNIQUE: Only 1 record per day per child
--     NOTE: Records are NEVER deleted (README §4.3 — audit trail)
-- ================================================================
CREATE TABLE attendance (
                            attendance_id   CHAR(36)    NOT NULL DEFAULT (UUID()),
                            child_id        CHAR(36)    NOT NULL,
                            recorded_by     CHAR(36)    NOT NULL,   -- teacher_id
                            date            DATE        NOT NULL,
                            status          ENUM('PRESENT','ABSENT') NOT NULL,
                            notes           TEXT,       -- correction note if mistake

                            created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            created_by  VARCHAR(128),
    -- soft delete columns kept for architectural consistency
    -- but should NEVER be set TRUE in production (README §4.3)
                            deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                            deleted_at  DATETIME    NULL,

                            PRIMARY KEY (attendance_id),
                            UNIQUE KEY uk_attendance_child_date (child_id, date),
                            CONSTRAINT fk_attendance_child FOREIGN KEY (child_id) REFERENCES child (child_id),
                            CONSTRAINT fk_attendance_teacher FOREIGN KEY (recorded_by) REFERENCES teacher (teacher_id)
);


-- ================================================================
-- 14. MEAL MENU  (Wickramasinghe)
--     When Admin saves the weekly menu plan
--     UNIQUE date: 1 menu record per day (Saturday optional)
-- ================================================================
CREATE TABLE meal_menu (
                           menu_id                 CHAR(36)    NOT NULL DEFAULT (UUID()),
                           date                    DATE        NOT NULL,
                           breakfast_details       TEXT,
                           lunch_details           TEXT,
                           evening_snack_details   TEXT,

                           created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           created_by  VARCHAR(128),
                           deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                           deleted_at  DATETIME    NULL,

                           PRIMARY KEY (menu_id),
                           UNIQUE KEY uk_meal_menu_date (date)
);


-- ================================================================
-- 15. MEAL CONSUMPTION LOG  (Wickramasinghe)
--     When Teacher marks whether a child ate or not
--     All 3 meals (breakfast/lunch/snack) are recorded separately
-- ================================================================
CREATE TABLE meal_consumption_log (
                                      consumption_id      CHAR(36)    NOT NULL DEFAULT (UUID()),
                                      child_id            CHAR(36)    NOT NULL,
                                      menu_id             CHAR(36)    NOT NULL,
                                      meal_type           ENUM('BREAKFAST','LUNCH','EVENING_SNACK') NOT NULL,
                                      consumption_status  ENUM('FULL_MEAL','PARTIAL','ATE_NONE') NOT NULL,
                                      note                TEXT,
                                      date                DATE        NOT NULL,

                                      created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      created_by  VARCHAR(128),
                                      deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                      deleted_at  DATETIME    NULL,

                                      PRIMARY KEY (consumption_id),
                                      UNIQUE KEY uk_consumption_child_menu_meal (child_id, menu_id, meal_type),
                                      CONSTRAINT fk_consumption_child FOREIGN KEY (child_id) REFERENCES child (child_id),
                                      CONSTRAINT fk_consumption_menu FOREIGN KEY (menu_id) REFERENCES meal_menu (menu_id)
);


-- ================================================================
-- 16. PAYMENT  (Peries)
--     Monthly billing per child
--     PaymentTransaction is created when Parent makes a payment
-- ================================================================
CREATE TABLE payment (
                         payment_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
                         parent_id       CHAR(36)        NOT NULL,
                         child_id        CHAR(36)        NOT NULL,
                         amount          DECIMAL(10,2)   NOT NULL,
                         status          ENUM('PENDING','PAID','OVERDUE') NOT NULL DEFAULT 'PENDING',
                         billing_month   VARCHAR(7)      NOT NULL,   -- format: '2025-06'

                         created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         created_by  VARCHAR(128),
                         deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                         deleted_at  DATETIME    NULL,

                         PRIMARY KEY (payment_id),
                         CONSTRAINT fk_payment_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id),
                         CONSTRAINT fk_payment_child FOREIGN KEY (child_id) REFERENCES child (child_id)
);


-- ================================================================
-- 17. PAYMENT TRANSACTION  (Peries)
--     Gateway transaction record (per payment attempt)
-- ================================================================
CREATE TABLE payment_transaction (
                                     txn_id              CHAR(36)    NOT NULL DEFAULT (UUID()),
                                     payment_id          CHAR(36)    NOT NULL,
                                     gateway_reference   VARCHAR(255),
                                     txn_time            DATETIME,
                                     txn_status          ENUM('SUCCESS','FAILED','PENDING') NOT NULL DEFAULT 'PENDING',
                                     requested_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     created_by  VARCHAR(128),
                                     deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                     deleted_at  DATETIME    NULL,

                                     PRIMARY KEY (txn_id),
                                     CONSTRAINT fk_txn_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id)
);


-- ================================================================
-- 18. CARD DETAILS  (Peries)
--     ER diagram: card_holder_name, card_last4, card_type,
--                 expiry_date, paid_via
-- ================================================================
CREATE TABLE card_details (
                              card_id             CHAR(36)        NOT NULL DEFAULT (UUID()),
                              payment_id          CHAR(36)        NOT NULL,
                              card_holder_name    VARCHAR(150)    NOT NULL,
                              card_last4          CHAR(4)         NOT NULL,
                              card_type           ENUM('VISA','MASTERCARD','AMEX','OTHER') NOT NULL,
                              expiry_date         VARCHAR(7),     -- MM/YYYY
                              paid_via            VARCHAR(50),

                              created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              created_by  VARCHAR(128),
                              deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                              deleted_at  DATETIME    NULL,

                              PRIMARY KEY (card_id),
                              CONSTRAINT fk_card_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id)
);


-- ================================================================
-- 19. NOTIFICATION  (Peries)
--     System notifications (meal plan added, attendance, payment…)
-- ================================================================
CREATE TABLE notification (
                              notification_id CHAR(36)    NOT NULL DEFAULT (UUID()),
                              body            TEXT        NOT NULL,

                              created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              created_by  VARCHAR(128),
                              deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                              deleted_at  DATETIME    NULL,

                              PRIMARY KEY (notification_id)
);


-- ================================================================
-- 20. NOTIFICATION TARGET  (Peries)
--     Notification targets — PARENT / TEACHER / ALL
--     target_ref_id: specific parent_id or teacher_id (NULL if ALL)
-- ================================================================
CREATE TABLE notification_target (
                                     target_id       CHAR(36)    NOT NULL DEFAULT (UUID()),
                                     notification_id CHAR(36)    NOT NULL,
                                     target_type     ENUM('PARENT','TEACHER','ALL') NOT NULL,
                                     target_ref_id   CHAR(36)    NULL,

                                     created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     created_by  VARCHAR(128),
                                     deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                     deleted_at  DATETIME    NULL,

                                     PRIMARY KEY (target_id),
                                     CONSTRAINT fk_target_notification FOREIGN KEY (notification_id) REFERENCES notification (notification_id)
);


-- ================================================================
-- 21. PUSH SUBSCRIPTION  (Peries)
--     FCM Web Push subscription per device / account
-- ================================================================
CREATE TABLE push_subscription (
                                   subscription_id CHAR(36)    NOT NULL DEFAULT (UUID()),
                                   account_id      CHAR(36)    NOT NULL,
                                   endpoint        TEXT        NOT NULL,
                                   p256dh_key      TEXT        NOT NULL,
                                   auth_key        TEXT        NOT NULL,
                                   status          ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',

                                   created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   created_by  VARCHAR(128),
                                   deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                                   deleted_at  DATETIME    NULL,

                                   PRIMARY KEY (subscription_id),
                                   CONSTRAINT fk_subscription_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);


-- ================================================================
-- 22. CHAT THREAD  (Peries)
--     Teacher ↔ Parent direct message thread
--     UNIQUE: Only 1 thread per pair in the entire system
-- ================================================================
CREATE TABLE chat_thread (
                             thread_id       CHAR(36)    NOT NULL DEFAULT (UUID()),
                             participant_one CHAR(36)    NOT NULL,   -- account_id
                             participant_two CHAR(36)    NOT NULL,   -- account_id

                             created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             created_by  VARCHAR(128),
                             deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                             deleted_at  DATETIME    NULL,

                             PRIMARY KEY (thread_id),
                             UNIQUE KEY uk_thread_participants (participant_one, participant_two),
                             CONSTRAINT fk_thread_p1 FOREIGN KEY (participant_one) REFERENCES account (account_id),
                             CONSTRAINT fk_thread_p2 FOREIGN KEY (participant_two) REFERENCES account (account_id)
);


-- ================================================================
-- 23. CHAT MESSAGE  (Peries)
--     Messages inside the thread
-- ================================================================
CREATE TABLE chat_message (
                              message_id  CHAR(36)    NOT NULL DEFAULT (UUID()),
                              thread_id   CHAR(36)    NOT NULL,
                              sender_id   CHAR(36)    NOT NULL,   -- account_id
                              content     TEXT        NOT NULL,
                              sent_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              created_by  VARCHAR(128),
                              deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                              deleted_at  DATETIME    NULL,

                              PRIMARY KEY (message_id),
                              CONSTRAINT fk_message_thread FOREIGN KEY (thread_id) REFERENCES chat_thread (thread_id),
                              CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES account (account_id)
);


-- ================================================================
-- 24. FAQ / CHATBOT  (Peries)
--     ER diagram: faq_id, question, answer, category
-- ================================================================
CREATE TABLE faq (
                     faq_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
                     question    TEXT            NOT NULL,
                     answer      TEXT            NOT NULL,
                     category    VARCHAR(100),

                     created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                     created_by  VARCHAR(128),
                     deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
                     deleted_at  DATETIME    NULL,

                     PRIMARY KEY (faq_id)
);


-- ================================================================
-- INDEXES  (README §5.4 — Required for frequent queries)
-- ================================================================

-- Auth
CREATE INDEX idx_otp_account         ON otp_token (account_id);

-- Child
CREATE INDEX idx_child_parent        ON child (parent_id);
CREATE INDEX idx_summary_child_date  ON child_summary (child_id, summary_date);

-- Activity (Wickramasinghe)
CREATE INDEX idx_assignment_teacher_date ON teacher_activity_assignment (teacher_id, assigned_date);
CREATE INDEX idx_assignment_status       ON teacher_activity_assignment (status);
CREATE INDEX idx_progress_child          ON activity_progress_log (child_id);

-- Attendance (Wickramasinghe)
CREATE INDEX idx_attendance_child_date   ON attendance (child_id, date);
CREATE INDEX idx_attendance_teacher_date ON attendance (recorded_by, date);

-- Meals (Wickramasinghe)
CREATE INDEX idx_consumption_child_date  ON meal_consumption_log (child_id, date);

-- Billing (Peries)
CREATE INDEX idx_payment_parent_status   ON payment (parent_id, status);
CREATE INDEX idx_payment_child           ON payment (child_id);
CREATE INDEX idx_txn_payment             ON payment_transaction (payment_id);

-- Comms (Peries)
CREATE INDEX idx_notification_target_notif ON notification_target (notification_id);
CREATE INDEX idx_push_sub_account          ON push_subscription (account_id);
CREATE INDEX idx_chat_msg_thread_time      ON chat_message (thread_id, sent_at);