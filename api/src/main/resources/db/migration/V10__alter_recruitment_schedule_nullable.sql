ALTER TABLE recruitment
    ALTER COLUMN application_start_time DROP NOT NULL,
    ALTER COLUMN application_end_time DROP NOT NULL,
    ALTER COLUMN application_result_time DROP NOT NULL,
    ALTER COLUMN interview_start_time DROP NOT NULL,
    ALTER COLUMN interview_end_time DROP NOT NULL,
    ALTER COLUMN final_result_time DROP NOT NULL;
