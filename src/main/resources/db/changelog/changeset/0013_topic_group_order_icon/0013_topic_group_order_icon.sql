ALTER TABLE consultingtypeservice.`topic_group`
    ADD COLUMN `sort_order` INT NOT NULL DEFAULT 0;
ALTER TABLE consultingtypeservice.`topic_group`
    ADD COLUMN `icon` MEDIUMTEXT NULL;
ALTER TABLE consultingtypeservice.`topic_group`
    ADD COLUMN `customized` TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE consultingtypeservice.`topic_group_x_topic`
    ADD COLUMN `sort_order` INT NOT NULL DEFAULT 0;

-- Existing categories keep the order they had (by id), their topics too (by topic id).
UPDATE consultingtypeservice.`topic_group` SET `sort_order` = `id`;
UPDATE consultingtypeservice.`topic_group_x_topic` placement
    JOIN (SELECT `group_id`, `topic_id`,
                 ROW_NUMBER() OVER (PARTITION BY `group_id` ORDER BY `topic_id`) - 1 AS position
          FROM consultingtypeservice.`topic_group_x_topic`) numbered
      ON numbered.`group_id` = placement.`group_id` AND numbered.`topic_id` = placement.`topic_id`
SET placement.`sort_order` = numbered.position;
