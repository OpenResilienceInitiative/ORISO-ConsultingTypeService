-- Until this release the Admin save ran every topic and category text through the OWASP HTML
-- sanitizer, which stores the text HTML-encoded: "Gesundheit & Sucht" became
-- "Gesundheit &amp; Sucht" and the registration showed "&amp;" (ORISO-ConsultingTypeService#207).
-- The sanitizer now keeps plain text, so decode the entities it wrote. Inside JSON text a quote
-- has to stay escaped (\"). &lt; and &gt; are dropped, as the sanitizer now drops angle brackets.
-- &amp; is decoded last, so text typed as "&amp;lt;" comes back as "&lt;", not as "<".
-- Hexadecimal references such as &#x1f600; (emoji) stay: these utf8 (3-byte) columns cannot
-- hold them, and the sanitizer keeps writing them that way.
-- The sanitizer decoded its input before encoding, so no value is encoded twice.

UPDATE consultingtypeservice.`topic_group`
SET `name` = IF(JSON_VALID(`name`),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`name`, '&#34;', CHAR(92, 34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`name`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'))
WHERE `name` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);';

UPDATE consultingtypeservice.`topic`
SET `name` = IF(JSON_VALID(`name`),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`name`, '&#34;', CHAR(92, 34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`name`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&')),
    `description` = IF(JSON_VALID(`description`),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`description`, '&#34;', CHAR(92, 34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`description`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&')),
    `titles_short` = IF(JSON_VALID(`titles_short`),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_short`, '&#34;', CHAR(92, 34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_short`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&')),
    `titles_long` = IF(JSON_VALID(`titles_long`),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_long`, '&#34;', CHAR(92, 34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_long`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&')),
    `titles_welcome` = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_welcome`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&'),
    `titles_dropdown` = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`titles_dropdown`, '&#34;', CHAR(34)), '&#39;', ''''), '&#43;', '+'), '&#61;', '='), '&#64;', '@'), '&#96;', '`'), '&#8175;', CONVERT(CHAR(0x1FEF USING utf32) USING utf8mb4)), '&lt;', ''), '&gt;', ''), '&amp;', '&')
WHERE `name` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);'
   OR `description` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);'
   OR `titles_short` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);'
   OR `titles_long` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);'
   OR `titles_welcome` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);'
   OR `titles_dropdown` REGEXP '&(amp|lt|gt|#34|#39|#43|#61|#64|#96|#8175);';
