CREATE TABLE user (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    password varchar(500) not null,
    full_name varchar(500) not null,
    email varchar(255) not null,
    role varchar(255) not null,
    created_at timestamp not null default current_timestamp,
    modified_at timestamp
);