-- Topic and category names were HTML-escaped on save ("Gesundheit &amp; Sucht"), and the Admin and
-- the registration showed the escape as text. Names are plain text now; turn the stored "&amp;"
-- back into "&". The sanitizer never nested the escape, so one pass is enough.
UPDATE consultingtypeservice.`topic_group`
SET `name` = REPLACE(`name`, '&amp;', '&')
WHERE `name` LIKE '%&amp;%';

UPDATE consultingtypeservice.`topic`
SET `name` = REPLACE(`name`, '&amp;', '&')
WHERE `name` LIKE '%&amp;%';
