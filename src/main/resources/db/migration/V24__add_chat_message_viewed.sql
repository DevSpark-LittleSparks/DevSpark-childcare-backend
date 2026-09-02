-- ================================================================
-- File    : V20__add_chat_message_viewed.sql
-- Purpose : Read/unread tracking for chat messages, used by the parent<->admin
--           messaging feature (this is the only migration touching
--           chat tables - the chat microservice reads/writes them but
--           never owns the schema).
-- ================================================================

ALTER TABLE chat_message ADD COLUMN IF NOT EXISTS viewed BOOLEAN NOT NULL DEFAULT FALSE AFTER sent_at;
CREATE INDEX IF NOT EXISTS idx_chat_msg_thread_viewed ON chat_message (thread_id, viewed);
