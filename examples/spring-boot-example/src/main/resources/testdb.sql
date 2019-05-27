CREATE DATABASE IF NOT EXISTS test;
create table IF NOT EXISTS encrypted
(
    id  int auto_increment primary key,
    encrypted_data varchar(256) default '' not null
);

