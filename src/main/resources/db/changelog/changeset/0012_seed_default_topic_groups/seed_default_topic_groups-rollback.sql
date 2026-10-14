DELETE FROM consultingtypeservice.topic_group_x_topic
WHERE group_id IN (1001, 1002, 1003);

DELETE FROM consultingtypeservice.topic_group
WHERE id IN (1001, 1002, 1003);
