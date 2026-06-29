DO $$
DECLARE
  table_rename record;
BEGIN
  FOR table_rename IN
    SELECT *
    FROM (VALUES
      ('"Review"', 'review'),
      ('"FAQ"', 'faq'),
      ('"Notification"', 'notification'),
      ('"RecruitPartIntroduction"', 'recruit_part_introduction'),
      ('"Generation"', 'generation'),
      ('"HomepageReview"', 'homepage_review'),
      ('"CoreValue"', 'core_value'),
      ('"MainNews"', 'main_news'),
      ('"PartType"', 'part_type'),
      ('"SoptStoryLike"', 'sopt_story_like'),
      ('"SoptStory"', 'sopt_story')
    ) AS renames(old_name, new_name)
  LOOP
    IF to_regclass(table_rename.old_name) IS NOT NULL
        AND to_regclass(table_rename.new_name) IS NULL THEN
      EXECUTE format(
          'ALTER TABLE %s RENAME TO %I',
          table_rename.old_name,
          table_rename.new_name
      );
    END IF;
  END LOOP;
END $$;

DO $$
DECLARE
  column_rename record;
BEGIN
  FOR column_rename IN
    SELECT *
    FROM (VALUES
      ('review', 'thumbnailUrl', 'thumbnail_url'),
      ('review', 'authorProfileImageUrl', 'author_profile_image_url'),
      ('review', 'createdAt', 'created_at'),
      ('review', 'updatedAt', 'updated_at'),
      ('notification', 'createdAt', 'created_at'),
      ('recruit_part_introduction', 'generationId', 'generation_id'),
      ('recruit_part_introduction', 'introductionContent', 'introduction_content'),
      ('recruit_part_introduction', 'introductionPreference', 'introduction_preference'),
      ('generation', 'headerImage', 'header_image'),
      ('generation', 'recruitHeaderImage', 'recruit_header_image'),
      ('generation', 'homeHeaderImage', 'home_header_image'),
      ('generation', 'darkModeKeyColor', 'dark_mode_key_color'),
      ('generation', 'darkModeTextColor', 'dark_mode_text_color'),
      ('generation', 'lightModeKeyColor', 'light_mode_key_color'),
      ('generation', 'lightModeTextColor', 'light_mode_text_color'),
      ('homepage_review', 'authorInfo', 'author_info'),
      ('homepage_review', 'createdAt', 'created_at'),
      ('homepage_review', 'updatedAt', 'updated_at'),
      ('core_value', 'generationId', 'generation_id'),
      ('core_value', 'detailDescription', 'detail_description'),
      ('core_value', 'imageUrl', 'image_url'),
      ('core_value', 'displayOrder', 'display_order'),
      ('main_news', 'createdAt', 'created_at'),
      ('main_news', 'updatedAt', 'updated_at'),
      ('part_type', 'generationId', 'generation_id'),
      ('part_type', 'partType', 'part_type'),
      ('sopt_story_like', 'soptStoryId', 'sopt_story_id'),
      ('sopt_story', 'thumbnailUrl', 'thumbnail_url'),
      ('sopt_story', 'soptStoryUrl', 'sopt_story_url'),
      ('sopt_story', 'likeCount', 'like_count'),
      ('sopt_story', 'createdAt', 'created_at')
    ) AS renames(table_name, old_name, new_name)
  LOOP
    IF to_regclass(column_rename.table_name) IS NOT NULL
        AND EXISTS (
          SELECT 1
          FROM information_schema.columns
          WHERE table_schema = current_schema()
            AND table_name = column_rename.table_name
            AND column_name = column_rename.old_name
        )
        AND NOT EXISTS (
          SELECT 1
          FROM information_schema.columns
          WHERE table_schema = current_schema()
            AND table_name = column_rename.table_name
            AND column_name = column_rename.new_name
        ) THEN
      EXECUTE format(
          'ALTER TABLE %I RENAME COLUMN %I TO %I',
          column_rename.table_name,
          column_rename.old_name,
          column_rename.new_name
      );
    END IF;
  END LOOP;
END $$;

DO $$
BEGIN
  IF to_regclass('sopt_story_like') IS NOT NULL
      AND EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE n.nspname = current_schema()
          AND t.relname = 'sopt_story_like'
          AND c.conname = 'uk_soptstory_like_ip'
      )
      AND NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE n.nspname = current_schema()
          AND t.relname = 'sopt_story_like'
          AND c.conname = 'uk_sopt_story_like_ip'
      ) THEN
    ALTER TABLE sopt_story_like
      RENAME CONSTRAINT uk_soptstory_like_ip TO uk_sopt_story_like_ip;
  END IF;
END $$;

DO $$
BEGIN
  IF to_regclass('recruit_part_introduction') IS NOT NULL
      AND to_regclass('part_type') IS NOT NULL
      AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'recruit_part_introduction'
          AND column_name = 'introduction_content'
      )
      AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'part_type'
          AND column_name = 'description'
      ) THEN
    UPDATE recruit_part_introduction introduction
    SET introduction_content = part.description
    FROM part_type part
    WHERE introduction.introduction_content IS NULL
      AND introduction.generation_id = part.generation_id
      AND introduction.part = part.part_type
      AND part.description IS NOT NULL;
  END IF;

  IF to_regclass('recruit_part_introduction') IS NOT NULL
      AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'recruit_part_introduction'
          AND column_name = 'introduction_preference'
      ) THEN
    UPDATE recruit_part_introduction
    SET introduction_preference = ''
    WHERE introduction_preference IS NULL;
  END IF;
END $$;

DO $$
DECLARE
  not_null_column record;
BEGIN
  FOR not_null_column IN
    SELECT *
    FROM (VALUES
      ('core_value', 'display_order'),
      ('faq', 'part'),
      ('faq', 'questions'),
      ('part_type', 'generation_id'),
      ('part_type', 'part_type'),
      ('part_type', 'description'),
      ('part_type', 'curriculums'),
      ('recruit_part_introduction', 'generation_id'),
      ('recruit_part_introduction', 'part'),
      ('recruit_part_introduction', 'introduction_content'),
      ('recruit_part_introduction', 'introduction_preference')
    ) AS columns(table_name, column_name)
  LOOP
    IF to_regclass(not_null_column.table_name) IS NOT NULL
        AND EXISTS (
          SELECT 1
          FROM information_schema.columns
          WHERE table_schema = current_schema()
            AND table_name = not_null_column.table_name
            AND column_name = not_null_column.column_name
        ) THEN
      EXECUTE format(
          'ALTER TABLE %I ALTER COLUMN %I SET NOT NULL',
          not_null_column.table_name,
          not_null_column.column_name
      );
    END IF;
  END LOOP;
END $$;
