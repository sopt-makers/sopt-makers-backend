ALTER TABLE sopt_letter_topic ADD COLUMN subtitle VARCHAR(255);

CREATE INDEX idx_sopt_letter_created_at ON sopt_letter (created_at);
