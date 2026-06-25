INSERT INTO consultingtypeservice.topic_group (id, name)
VALUES
  (1001, '{"de":"Familie, Kinder und Jugend","en":"Family, children and youth"}'),
  (1002, '{"de":"Gesundheit, Pflege und Vorsorge","en":"Health, care and advance planning"}'),
  (1003, '{"de":"Soziales, Migration und besondere Lebenslagen","en":"Social support, migration and special life situations"}')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  update_date = UTC_TIMESTAMP();

INSERT INTO consultingtypeservice.topic_group_x_topic (group_id, topic_id)
SELECT 1001, t.id
FROM consultingtypeservice.topic t
WHERE t.internal_identifier IN (
  'children-youth',
  'u25-suicide-prevention',
  'child-youth-rehab',
  'parents-family',
  'pregnancy',
  'cures-parents'
)
AND NOT EXISTS (
  SELECT 1
  FROM consultingtypeservice.topic_group_x_topic existing
  WHERE existing.group_id = 1001
    AND existing.topic_id = t.id
);

INSERT INTO consultingtypeservice.topic_group_x_topic (group_id, topic_id)
SELECT 1002, t.id
FROM consultingtypeservice.topic t
WHERE t.internal_identifier IN (
  'legal-guardianship',
  'hospice-palliative',
  'hiv-aids',
  'disability-psych',
  'life-in-old-age'
)
AND NOT EXISTS (
  SELECT 1
  FROM consultingtypeservice.topic_group_x_topic existing
  WHERE existing.group_id = 1002
    AND existing.topic_id = t.id
);

INSERT INTO consultingtypeservice.topic_group_x_topic (group_id, topic_id)
SELECT 1003, t.id
FROM consultingtypeservice.topic t
WHERE t.internal_identifier IN (
  'general-social',
  'men-boys',
  'migration',
  'offending',
  'debt'
)
AND NOT EXISTS (
  SELECT 1
  FROM consultingtypeservice.topic_group_x_topic existing
  WHERE existing.group_id = 1003
    AND existing.topic_id = t.id
);
