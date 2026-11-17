-- Until this release the welcome and registration-dropdown titles were written JSON-encoded
-- ("Sucht" including the quotes) but read back verbatim, so the quotes showed in the registration.
-- Every save through the Admin wrapped them once more. Unwrap only values that are a valid JSON
-- string. Plain text (not valid JSON) stays as it is. Each pass removes one layer. With the
-- 255-character columns more than six layers cannot exist.
UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');

UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');

UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');

UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');

UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');

UPDATE consultingtypeservice.`topic`
SET `titles_welcome`  = IF(JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING',
                           JSON_UNQUOTE(`titles_welcome`), `titles_welcome`),
    `titles_dropdown` = IF(JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING',
                           JSON_UNQUOTE(`titles_dropdown`), `titles_dropdown`)
WHERE (JSON_VALID(`titles_welcome`) AND JSON_TYPE(`titles_welcome`) = 'STRING')
   OR (JSON_VALID(`titles_dropdown`) AND JSON_TYPE(`titles_dropdown`) = 'STRING');
