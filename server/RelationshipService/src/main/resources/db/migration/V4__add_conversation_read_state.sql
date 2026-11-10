ALTER TABLE conversation_participants
    ADD COLUMN last_read_message_id INT NULL AFTER left_at;
