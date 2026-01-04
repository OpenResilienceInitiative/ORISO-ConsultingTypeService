ALTER TABLE consultingtypeservice.`topic`
    ADD titles_short varchar (255) NULL,
    ADD titles_long varchar (255) NULL,
    ADD titles_welcome varchar (255) NULL,
    ADD titles_dropdown varchar (255)  NULL,
    ADD slug varchar(255) NULL;

ALTER TABLE consultingtypeservice.`topic`
MODIFY COLUMN internal_identifier varchar(255);