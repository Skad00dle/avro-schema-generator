create table comment_table
(
    `id`                  int         not null  comment 'Id for the comment table.',
    `first_name`          varchar(50) not null,
    `last_name`           varchar(50),
    `address`             varchar(50) default null,
    `created`             timestamp   not null,
    `updated`             timestamp,
    `decimal_field`       decimal(20, 3),
    `other_decimal_field` decimal
)comment='Table with comments.';;
