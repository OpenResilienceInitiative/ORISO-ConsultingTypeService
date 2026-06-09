UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Boys'' and men''s counseling')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Jungen- und Männerberatung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Child & youth rehabilitation')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Kind & Jugend Reha';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Health cures for mothers & fathers')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Kuren Mütter & Väter';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Emigration / return & onward migration')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Aus-/Rück- & Weiterwanderung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Disability & psychological impairment')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Behinderung & psych. Beeinträchtigung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'HIV & AIDS')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'HIV & Aids';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Parents and family')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Eltern und Familie';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Hospice & palliative care counseling')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Hospiz & Palliativberatung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Children and youth counseling')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Kinder und Jugendlicheberatung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Legal guardianship & advance care planning')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Rechtliche Betreuung & Vorsorge';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Living in old age')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Leben im Alter';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Debt')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Schulden';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Social counseling')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Sozialberatung';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Pregnancy')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Schwangerschaft';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'Offending / criminal-justice support')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'Straffälligkeit';

UPDATE consultingtypeservice.`topic`
SET name = JSON_SET(name, '$.en', 'U25 suicide prevention')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) = 'U25 Suizidprävention';
