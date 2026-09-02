-- ================================================================
--  File    : V37__populate_faq_keywords_and_expand_coverage.sql
--  Purpose : ChatbotService.matchFaq() scores each FAQ row by how many
--            comma-separated `keywords` appear in the user's message.
--            Every row currently has keywords = '' (added by V17/V18
--            but never backfilled), so `"".split(",")` always yields
--            an empty token and the score never leaves 0 — the FAQ
--            fast-path has been dead code since it was introduced;
--            every message has been falling through to the Groq AI
--            call, even ones with a perfect canned answer available.
--            `role` was also left NULL on everything, so role-specific
--            answers (e.g. admin-only flows) were being offered to
--            every role. This migration backfills both, and adds a
--            handful of additional FAQ rows for common doubts that
--            were verified against real, working endpoints/pages
--            (not invented) but weren't covered yet.
-- ================================================================

-- ── Backfill keywords + role on the 28 existing rows ──────────────

UPDATE faq SET keywords = 'log in,login,sign in,signin', role = NULL
    WHERE question = 'How do I log in?';

UPDATE faq SET keywords = 'otp,verification code,one time code,verify email,verify my email', role = NULL
    WHERE question = 'What is the OTP step when I log in?';

UPDATE faq SET keywords = 'update profile,edit profile,profile picture,change profile,my details', role = NULL
    WHERE question = 'How do I update my profile?';

UPDATE faq SET keywords = 'support,help,contact support,billing issue,account issue', role = NULL
    WHERE question = 'How do I get support?';

UPDATE faq SET keywords = 'register teacher,teacher signup,sign up teacher,become a teacher', role = NULL
    WHERE question = 'How do I register as a teacher?';

UPDATE faq SET keywords = 'register parent,parent signup,sign up parent,guardian email', role = NULL
    WHERE question = 'How do I register as a parent?';

UPDATE faq SET keywords = 'register child,add child,new child,enroll child,admission', role = 'ADMIN'
    WHERE question = 'How do I register a new child?';

UPDATE faq SET keywords = 'approve parent,parent registration,parent request,pending parent', role = 'ADMIN'
    WHERE question = 'How do I approve a parent''s registration?';

UPDATE faq SET keywords = 'approve teacher,teacher registration,teacher request,pending teacher', role = 'ADMIN'
    WHERE question = 'How do I approve a teacher''s registration?';

UPDATE faq SET keywords = 'meal menu,weekly menu,set menu,food menu,breakfast,lunch,evening snack', role = 'ADMIN'
    WHERE question = 'How do I set the weekly meal menu?';

UPDATE faq SET keywords = 'tomorrow menu,menu notification,food notification,what''s for lunch,meal notification', role = 'PARENT'
    WHERE question = 'How do parents find out about tomorrow''s menu?';

UPDATE faq SET keywords = 'create activity,new activity,add activity,activity category', role = 'ADMIN'
    WHERE question = 'How do I create a new activity?';

UPDATE faq SET keywords = 'assign activity,activity assignment,schedule activity,publish activity', role = 'ADMIN'
    WHERE question = 'How do I assign an activity to a teacher?';

UPDATE faq SET keywords = 'learning progress,progress page,daily progress,activity list,center wide', role = 'ADMIN'
    WHERE question = 'What can I see on the Learning Progress page?';

UPDATE faq SET keywords = 'mark attendance,attendance,check in,check out,present,absent,half day', role = 'TEACHER'
    WHERE question = 'How do I mark attendance?';

UPDATE faq SET keywords = 'edit attendance,past attendance,change attendance,attendance record', role = 'TEACHER'
    WHERE question = 'Can I edit a past attendance record?';

UPDATE faq SET keywords = 'log meal,meal consumption,child ate,finalize meal,mark meal,mark all full', role = 'TEACHER'
    WHERE question = 'How do I log what a child ate?';

UPDATE faq SET keywords = 'allergy,allergy warning,red warning,medical condition,safety alert', role = 'TEACHER'
    WHERE question = 'What does the red allergy warning mean?';

UPDATE faq SET keywords = 'grade activity,grading,performance level,rate child,mark all good', role = 'TEACHER'
    WHERE question = 'How do I grade a child''s activity performance?';

UPDATE faq SET keywords = 'grading disabled,buttons disabled,cannot grade,absent grading', role = 'TEACHER'
    WHERE question = 'Why are some activity grading buttons disabled?';

UPDATE faq SET keywords = 'teacher dashboard,my dashboard,assigned activities,dashboard show', role = 'TEACHER'
    WHERE question = 'What does my Teacher Dashboard show?';

UPDATE faq SET keywords = 'child progress,view progress,progress report,my child', role = 'PARENT'
    WHERE question = 'How do I view my child''s progress?';

UPDATE faq SET keywords = 'average performance,performance mean,grading percentage', role = 'PARENT'
    WHERE question = 'What does "Average Performance" mean on the progress page?';

UPDATE faq SET keywords = 'meals eaten,meal percentage,meals mean', role = 'PARENT'
    WHERE question = 'What does "Meals Eaten" mean on the progress page?';

UPDATE faq SET keywords = 'another child,other child,see other,child data privacy', role = 'PARENT'
    WHERE question = 'Can I see another child''s progress?';

UPDATE faq SET keywords = 'balance,pay balance,outstanding balance,pay now,invoice', role = 'PARENT'
    WHERE question = 'How do I check or pay my balance?';

UPDATE faq SET keywords = 'payment method,add card,add bank,stripe,card details', role = 'PARENT'
    WHERE question = 'How do I add a payment method?';

UPDATE faq SET keywords = 'payment statement,download statement,invoice pdf,billing statement', role = 'PARENT'
    WHERE question = 'How do I download my payment statement?';

-- ── New FAQ rows: common doubts not yet covered, grounded in verified,
--    working features (NotificationController, BroadcastPortal.tsx,
--    submit-request endpoint, SettingsDrawer.tsx) ──────────────────

INSERT INTO faq (faq_id, role, keywords, question, answer) VALUES
(UUID_TO_BIN(UUID()), NULL,
 'who are you,your name,what is your name,what can you do,what can you help with',
 'Who are you?',
 'I''m the LittleSparks Assistant. I can answer questions about using this childcare management system based on your role — just ask me about attendance, meals, progress, payments, notifications, or your account.'),

(UUID_TO_BIN(UUID()), NULL,
 'what is littlesparks,what is this app,what is this platform,about littlesparks,about this system',
 'What is LittleSparks?',
 'LittleSparks is a childcare management system used by a childcare center to manage children, staff, attendance, activities, meals, learning progress, and communication between the center and parents. It has three portals — Parent, Teacher, and Admin.'),

(UUID_TO_BIN(UUID()), NULL,
 'notifications,alerts,my alerts,unread,notification bell',
 'How do I view my notifications or alerts?',
 'Open the notification bell icon in your dashboard header to see your alerts — broadcasts from the admin and any updates targeted to you. Click one to mark it as read.'),

(UUID_TO_BIN(UUID()), 'ADMIN',
 'broadcast,announcement,send notification,notify everyone,broadcast portal',
 'How do I send an announcement to parents or teachers?',
 'Go to the Broadcast Portal, write your title and message, choose whether it targets everyone, only parents, or only teachers, and send it — it goes out instantly as a real-time notification.'),

(UUID_TO_BIN(UUID()), NULL,
 'contact admin,submit request,report a problem,need help from admin,raise an issue',
 'How do I contact the admin if I have a problem?',
 'From your Profile page, use the request form to submit a message describing your issue — it goes straight into the Admin''s request list for review.'),

(UUID_TO_BIN(UUID()), NULL,
 'settings,theme,dark mode,language,currency,timezone,preferences',
 'How do I change app settings like theme, language, or currency?',
 'Open the Settings panel from your dashboard to change your theme, language, currency, and timezone preferences.');
