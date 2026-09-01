-- ================================================================
--  File    : V38__add_password_reset_faq.sql
--  Purpose : "Forgot my password" was falling into the guest chatbot's
--            generic "you need to log in first" catch-all, because
--            devspark-docs.md never mentioned password reset — a
--            logical trap, since a locked-out user can't log in to
--            ask about it. Fixed on the AI side in
--            devspark-docs.md + ChatbotService's guest system prompt;
--            this adds the matching fast-path FAQ entry.
-- ================================================================

INSERT INTO faq (faq_id, role, keywords, question, answer) VALUES
(UUID_TO_BIN(UUID()), NULL,
 'forgot password,reset password,forgot my password,cant log in,can''t log in,locked out,change my password',
 'What if I forget my password?',
 'On the Login page, click "Forgot Password?" and enter your registered email — you''ll get a link to reset it. You can do this even if you''re not currently logged in.');
