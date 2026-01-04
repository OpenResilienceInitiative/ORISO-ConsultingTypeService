DROP TABLE IF EXISTS topic_group_x_topic;
DROP TABLE IF EXISTS `topic`;
DROP TABLE IF EXISTS `topic_group`;
DROP SEQUENCE IF EXISTS sequence_topic_group_x_topic;
DROP SEQUENCE IF EXISTS sequence_topic_group;

CREATE TABLE IF NOT EXISTS `topic`
(
    `id`                     bigint   NOT NULL,
    `tenant_id`              bigint   NULL,
    `name`                   varchar(100) NOT NULL,
    `description`            varchar(100) NULL,
    `status`                 varchar(20),
    `create_date`            datetime     NOT NULL,
    `update_date`            datetime     NULL,
    `internal_identifier`    varchar(50)  NULL,
    `fallback_agency_id`     bigint   NULL,
    `fallback_url`           varchar(200) NULL,
    `welcome_message`        varchar(200) NULL,
    `send_next_step_message` boolean DEFAULT FALSE,
    `titles_short`           varchar(255) NOT NULL,
    `titles_long`            varchar(255) NOT NULL,
    `titles_welcome`         varchar(255) NOT NULL,
    `titles_dropdown`        varchar(255) NOT NULL,
    `slug`                   varchar(255) NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `topic`
    ALTER COLUMN `create_date` SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `topic`
    ALTER COLUMN `update_date` SET DEFAULT CURRENT_TIMESTAMP;

CREATE SEQUENCE IF NOT EXISTS sequence_topic
    INCREMENT BY 1
    START WITH 100000;

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `fallback_agency_id`,
                   `fallback_url`, `welcome_message`, `send_next_step_message`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`,`slug`)
VALUES (1, '1', '{"de" : "de an active topic", "en": "en an active topic"}',
        '{"de" : "de description", "en": "en description"}', 'ACTIVE',
        '2022-06-02', 1, 'https://www.google.com', 'Welcome', TRUE, '{"de" : "1-short", "en": "1-short-en"}',
        '{"de" : "1-long", "en": "1-long-en"}', '1-welcome', '1-dropdown','1-slug');

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`, `slug`)
VALUES (2, '1', '{"de" : "de not an active topic", "en": "en not an active topic"}',
        '{"de" : "de description", "en": "en description"}', 'INACTIVE', '2022-06-02', '{"de" : "2-short", "en": "2-short-en"}', '{"de" : "2-short", "en": "2-long-en"}',
        '2-welcome', '2-dropdown', '2-slug');

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`, `slug`)
VALUES (3, '2', '{"de" : "de another topic"}', '{"de" : "de description"}', 'ACTIVE', '2022-06-02',
        '{"de" : "3-short", "en": "3-short-en"}', '{"de" : "3-long", "en": "3-long-en"}', '3-welcome', '3-dropdown', '3-slug');

CREATE TABLE IF NOT EXISTS topic_group
(
    `id`          bigint   NOT NULL,
    `name`        varchar(100) NOT NULL,
    `create_date` datetime     NOT NULL,
    `tenant_id`   bigint   NULL,
    `update_date` datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE SEQUENCE sequence_topic_group
    INCREMENT BY 1
    START WITH 1;

CREATE TABLE IF NOT EXISTS topic_group_x_topic
(
    group_id bigint NOT NULL,
    topic_id bigint NOT NULL,
    foreign key (group_id) references topic_group (id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (topic_id) references topic (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE sequence_topic_group_x_topic
    INCREMENT BY 1
    START WITH 1;