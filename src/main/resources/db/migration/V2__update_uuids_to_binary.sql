-- ================================================================
--  DevSpark — Child Care Management System
--  File    : V2__update_uuids_to_binary.sql
--  Purpose : Convert all CHAR(36) UUID columns to BINARY(16)
--            to comply with Hibernate 6.x standard mapping.
--            Removes the MySQL DEFAULT (UUID()) as Hibernate
--            will now generate the Binary UUIDs automatically.
-- ================================================================

-- ----------------------------------------------------------------
-- STEP 1: DROP ALL FOREIGN KEYS
-- We must drop these constraints before modifying the primary keys
-- ----------------------------------------------------------------
ALTER TABLE otp_token DROP FOREIGN KEY fk_otp_account;
ALTER TABLE admin DROP FOREIGN KEY fk_admin_account;
ALTER TABLE parent DROP FOREIGN KEY fk_parent_account;
ALTER TABLE child DROP FOREIGN KEY fk_child_parent;
ALTER TABLE child_summary DROP FOREIGN KEY fk_summary_child;
ALTER TABLE teacher DROP FOREIGN KEY fk_teacher_account;
ALTER TABLE teacher_activity_assignment DROP FOREIGN KEY fk_assignment_teacher;
ALTER TABLE teacher_activity_assignment DROP FOREIGN KEY fk_assignment_activity;
ALTER TABLE activity_progress_log DROP FOREIGN KEY fk_log_assignment;
ALTER TABLE activity_progress_log DROP FOREIGN KEY fk_log_child;
ALTER TABLE attendance DROP FOREIGN KEY fk_attendance_child;
ALTER TABLE attendance DROP FOREIGN KEY fk_attendance_teacher;
ALTER TABLE meal_consumption_log DROP FOREIGN KEY fk_consumption_child;
ALTER TABLE meal_consumption_log DROP FOREIGN KEY fk_consumption_menu;
ALTER TABLE payment DROP FOREIGN KEY fk_payment_parent;
ALTER TABLE payment DROP FOREIGN KEY fk_payment_child;
ALTER TABLE payment_transaction DROP FOREIGN KEY fk_txn_payment;
ALTER TABLE card_details DROP FOREIGN KEY fk_card_payment;
ALTER TABLE notification_target DROP FOREIGN KEY fk_target_notification;
ALTER TABLE push_subscription DROP FOREIGN KEY fk_subscription_account;
ALTER TABLE chat_thread DROP FOREIGN KEY fk_thread_p1;
ALTER TABLE chat_thread DROP FOREIGN KEY fk_thread_p2;
ALTER TABLE chat_message DROP FOREIGN KEY fk_message_thread;
ALTER TABLE chat_message DROP FOREIGN KEY fk_message_sender;

-- ----------------------------------------------------------------
-- STEP 2: DROP UNIQUE KEYS THAT CONTAIN UUIDs
-- ----------------------------------------------------------------
ALTER TABLE admin DROP INDEX uk_admin_account;
ALTER TABLE parent DROP INDEX uk_parent_account;
ALTER TABLE teacher DROP INDEX uk_teacher_account;
ALTER TABLE teacher_activity_assignment DROP INDEX uk_assignment_teacher_activity_date;
ALTER TABLE activity_progress_log DROP INDEX uk_log_assignment_child;
ALTER TABLE attendance DROP INDEX uk_attendance_child_date;
ALTER TABLE meal_consumption_log DROP INDEX uk_consumption_child_menu_meal;
ALTER TABLE chat_thread DROP INDEX uk_thread_participants;

-- ----------------------------------------------------------------
-- STEP 3: DROP INDEXES THAT CONTAIN UUIDs
-- ----------------------------------------------------------------
DROP INDEX idx_otp_account ON otp_token;
DROP INDEX idx_child_parent ON child;
DROP INDEX idx_summary_child_date ON child_summary;
DROP INDEX idx_assignment_teacher_date ON teacher_activity_assignment;
DROP INDEX idx_progress_child ON activity_progress_log;
DROP INDEX idx_attendance_child_date ON attendance;
DROP INDEX idx_attendance_teacher_date ON attendance;
DROP INDEX idx_consumption_child_date ON meal_consumption_log;
DROP INDEX idx_payment_parent_status ON payment;
DROP INDEX idx_payment_child ON payment;
DROP INDEX idx_txn_payment ON payment_transaction;
DROP INDEX idx_notification_target_notif ON notification_target;
DROP INDEX idx_push_sub_account ON push_subscription;
DROP INDEX idx_chat_msg_thread_time ON chat_message;

-- ----------------------------------------------------------------
-- STEP 4: MODIFY ALL UUID COLUMNS TO BINARY(16)
-- ----------------------------------------------------------------
ALTER TABLE account MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE otp_token MODIFY otp_id BINARY(16) NOT NULL, MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE admin MODIFY admin_id BINARY(16) NOT NULL, MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE parent_registration_request MODIFY request_id BINARY(16) NOT NULL;
ALTER TABLE parent MODIFY parent_id BINARY(16) NOT NULL, MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE child MODIFY child_id BINARY(16) NOT NULL, MODIFY parent_id BINARY(16) NOT NULL;
ALTER TABLE child_summary MODIFY summary_id BINARY(16) NOT NULL, MODIFY child_id BINARY(16) NOT NULL;
ALTER TABLE teacher_invitation MODIFY invitation_id BINARY(16) NOT NULL;
ALTER TABLE teacher MODIFY teacher_id BINARY(16) NOT NULL, MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE activity MODIFY activity_id BINARY(16) NOT NULL;
ALTER TABLE teacher_activity_assignment MODIFY assignment_id BINARY(16) NOT NULL, MODIFY teacher_id BINARY(16) NOT NULL, MODIFY activity_id BINARY(16) NOT NULL;
ALTER TABLE activity_progress_log MODIFY log_id BINARY(16) NOT NULL, MODIFY assignment_id BINARY(16) NOT NULL, MODIFY child_id BINARY(16) NOT NULL;
ALTER TABLE attendance MODIFY attendance_id BINARY(16) NOT NULL, MODIFY child_id BINARY(16) NOT NULL, MODIFY recorded_by BINARY(16) NOT NULL;
ALTER TABLE meal_menu MODIFY menu_id BINARY(16) NOT NULL;
ALTER TABLE meal_consumption_log MODIFY consumption_id BINARY(16) NOT NULL, MODIFY child_id BINARY(16) NOT NULL, MODIFY menu_id BINARY(16) NOT NULL;
ALTER TABLE payment MODIFY payment_id BINARY(16) NOT NULL, MODIFY parent_id BINARY(16) NOT NULL, MODIFY child_id BINARY(16) NOT NULL;
ALTER TABLE payment_transaction MODIFY txn_id BINARY(16) NOT NULL, MODIFY payment_id BINARY(16) NOT NULL;
ALTER TABLE card_details MODIFY card_id BINARY(16) NOT NULL, MODIFY payment_id BINARY(16) NOT NULL;
ALTER TABLE notification MODIFY notification_id BINARY(16) NOT NULL;
ALTER TABLE notification_target MODIFY target_id BINARY(16) NOT NULL, MODIFY notification_id BINARY(16) NOT NULL, MODIFY target_ref_id BINARY(16) NULL;
ALTER TABLE push_subscription MODIFY subscription_id BINARY(16) NOT NULL, MODIFY account_id BINARY(16) NOT NULL;
ALTER TABLE chat_thread MODIFY thread_id BINARY(16) NOT NULL, MODIFY participant_one BINARY(16) NOT NULL, MODIFY participant_two BINARY(16) NOT NULL;
ALTER TABLE chat_message MODIFY message_id BINARY(16) NOT NULL, MODIFY thread_id BINARY(16) NOT NULL, MODIFY sender_id BINARY(16) NOT NULL;
ALTER TABLE faq MODIFY faq_id BINARY(16) NOT NULL;

-- ----------------------------------------------------------------
-- STEP 5: RE-ADD UNIQUE KEYS
-- ----------------------------------------------------------------
ALTER TABLE admin ADD CONSTRAINT uk_admin_account UNIQUE (account_id);
ALTER TABLE parent ADD CONSTRAINT uk_parent_account UNIQUE (account_id);
ALTER TABLE teacher ADD CONSTRAINT uk_teacher_account UNIQUE (account_id);
ALTER TABLE teacher_activity_assignment ADD CONSTRAINT uk_assignment_teacher_activity_date UNIQUE (teacher_id, activity_id, assigned_date);
ALTER TABLE activity_progress_log ADD CONSTRAINT uk_log_assignment_child UNIQUE (assignment_id, child_id);
ALTER TABLE attendance ADD CONSTRAINT uk_attendance_child_date UNIQUE (child_id, date);
ALTER TABLE meal_consumption_log ADD CONSTRAINT uk_consumption_child_menu_meal UNIQUE (child_id, menu_id, meal_type);
ALTER TABLE chat_thread ADD CONSTRAINT uk_thread_participants UNIQUE (participant_one, participant_two);

-- ----------------------------------------------------------------
-- STEP 6: RE-ADD INDEXES
-- ----------------------------------------------------------------
CREATE INDEX idx_otp_account ON otp_token (account_id);
CREATE INDEX idx_child_parent ON child (parent_id);
CREATE INDEX idx_summary_child_date ON child_summary (child_id, summary_date);
CREATE INDEX idx_assignment_teacher_date ON teacher_activity_assignment (teacher_id, assigned_date);
CREATE INDEX idx_progress_child ON activity_progress_log (child_id);
CREATE INDEX idx_attendance_child_date ON attendance (child_id, date);
CREATE INDEX idx_attendance_teacher_date ON attendance (recorded_by, date);
CREATE INDEX idx_consumption_child_date ON meal_consumption_log (child_id, date);
CREATE INDEX idx_payment_parent_status ON payment (parent_id, status);
CREATE INDEX idx_payment_child ON payment (child_id);
CREATE INDEX idx_txn_payment ON payment_transaction (payment_id);
CREATE INDEX idx_notification_target_notif ON notification_target (notification_id);
CREATE INDEX idx_push_sub_account ON push_subscription (account_id);
CREATE INDEX idx_chat_msg_thread_time ON chat_message (thread_id, sent_at);

-- ----------------------------------------------------------------
-- STEP 7: RE-ADD FOREIGN KEYS
-- ----------------------------------------------------------------
ALTER TABLE otp_token ADD CONSTRAINT fk_otp_account FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE admin ADD CONSTRAINT fk_admin_account FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE parent ADD CONSTRAINT fk_parent_account FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE child ADD CONSTRAINT fk_child_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id);
ALTER TABLE child_summary ADD CONSTRAINT fk_summary_child FOREIGN KEY (child_id) REFERENCES child (child_id);
ALTER TABLE teacher ADD CONSTRAINT fk_teacher_account FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE teacher_activity_assignment ADD CONSTRAINT fk_assignment_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id);
ALTER TABLE teacher_activity_assignment ADD CONSTRAINT fk_assignment_activity FOREIGN KEY (activity_id) REFERENCES activity (activity_id);
ALTER TABLE activity_progress_log ADD CONSTRAINT fk_log_assignment FOREIGN KEY (assignment_id) REFERENCES teacher_activity_assignment (assignment_id);
ALTER TABLE activity_progress_log ADD CONSTRAINT fk_log_child FOREIGN KEY (child_id) REFERENCES child (child_id);
ALTER TABLE attendance ADD CONSTRAINT fk_attendance_child FOREIGN KEY (child_id) REFERENCES child (child_id);
ALTER TABLE attendance ADD CONSTRAINT fk_attendance_teacher FOREIGN KEY (recorded_by) REFERENCES teacher (teacher_id);
ALTER TABLE meal_consumption_log ADD CONSTRAINT fk_consumption_child FOREIGN KEY (child_id) REFERENCES child (child_id);
ALTER TABLE meal_consumption_log ADD CONSTRAINT fk_consumption_menu FOREIGN KEY (menu_id) REFERENCES meal_menu (menu_id);
ALTER TABLE payment ADD CONSTRAINT fk_payment_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id);
ALTER TABLE payment ADD CONSTRAINT fk_payment_child FOREIGN KEY (child_id) REFERENCES child (child_id);
ALTER TABLE payment_transaction ADD CONSTRAINT fk_txn_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id);
ALTER TABLE card_details ADD CONSTRAINT fk_card_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id);
ALTER TABLE notification_target ADD CONSTRAINT fk_target_notification FOREIGN KEY (notification_id) REFERENCES notification (notification_id);
ALTER TABLE push_subscription ADD CONSTRAINT fk_subscription_account FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE chat_thread ADD CONSTRAINT fk_thread_p1 FOREIGN KEY (participant_one) REFERENCES account (account_id);
ALTER TABLE chat_thread ADD CONSTRAINT fk_thread_p2 FOREIGN KEY (participant_two) REFERENCES account (account_id);
ALTER TABLE chat_message ADD CONSTRAINT fk_message_thread FOREIGN KEY (thread_id) REFERENCES chat_thread (thread_id);
ALTER TABLE chat_message ADD CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES account (account_id);