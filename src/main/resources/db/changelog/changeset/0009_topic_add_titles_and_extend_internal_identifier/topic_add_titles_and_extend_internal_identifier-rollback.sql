ALTER TABLE consultingtypeservice.`topic`
    DROP IF EXISTS titles_short,
    DROP IF EXISTS titles_long,
    DROP IF EXISTS titles_welcome,
    DROP IF EXISTS titles_dropdown,
    DROP IF EXISTS slug;

ALTER TABLE consultingtypeservice.`topic`
MODIFY COLUMN internal_identifier varchar(50);