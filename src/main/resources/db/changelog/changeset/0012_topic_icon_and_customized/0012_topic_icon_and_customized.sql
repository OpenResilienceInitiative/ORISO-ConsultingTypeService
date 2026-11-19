ALTER TABLE consultingtypeservice.`topic`
    ADD COLUMN `icon` MEDIUMTEXT NULL;
ALTER TABLE consultingtypeservice.`topic`
    ADD COLUMN `customized` TINYINT(1) NOT NULL DEFAULT 0;
