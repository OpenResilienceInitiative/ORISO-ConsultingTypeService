UPDATE consultingtypeservice.`topic`
SET name = JSON_REMOVE(name, '$.en')
WHERE JSON_UNQUOTE(JSON_EXTRACT(name, '$.de')) IN (
  'Jungen- und Männerberatung',
  'Kind & Jugend Reha',
  'Kuren Mütter & Väter',
  'Aus-/Rück- & Weiterwanderung',
  'Behinderung & psych. Beeinträchtigung',
  'HIV & Aids',
  'Eltern und Familie',
  'Hospiz & Palliativberatung',
  'Kinder und Jugendlicheberatung',
  'Rechtliche Betreuung & Vorsorge',
  'Leben im Alter',
  'Schulden',
  'Sozialberatung',
  'Schwangerschaft',
  'Straffälligkeit',
  'U25 Suizidprävention'
);
