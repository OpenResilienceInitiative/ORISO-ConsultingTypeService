ALTER TABLE consultingtypeservice.`topic`
    MODIFY COLUMN titles_short varchar (4092);
ALTER TABLE consultingtypeservice.`topic`
    MODIFY COLUMN titles_long varchar (4092);
ALTER TABLE consultingtypeservice.`topic`
    MODIFY COLUMN name varchar (4092);
ALTER TABLE consultingtypeservice.`topic`
    MODIFY COLUMN description varchar (4092);
ALTER TABLE consultingtypeservice.`topic_group`
    MODIFY COLUMN name varchar (4092);

