CREATE TABLE instructor (
    id bigint auto_increment primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    info varchar(500) not null,
    user_id bigint(11) not null,
    FOREIGN KEY (user_id) REFERENCES user(Id)
);

CREATE TABLE course (
    id bigint auto_increment primary key,
    name varchar(255) not null
);

CREATE TABLE instructor_course (
    instructor_id bigint not null,
    course_id bigint not null,
    FOREIGN KEY (instructor_id) REFERENCES instructor(Id),
    FOREIGN KEY (course_id) REFERENCES course(Id),
    primary key (instructor_id, course_id)
);

CREATE TABLE student (
    id bigint auto_increment primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    group_name varchar(255) not null,
    faculty varchar(255) not null,
    user_id bigint(11) not null,
    FOREIGN KEY (user_id) REFERENCES user(Id)
);

CREATE TABLE student_course (
    student_id bigint not null,
    course_id bigint not null,
    FOREIGN KEY (student_id) REFERENCES student(Id),
    FOREIGN KEY (course_id) REFERENCES course(Id),
    primary key (student_id, course_id)
);

CREATE TABLE lesson (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    description longtext not null,
    course_id bigint not null,
    FOREIGN KEY (course_id) REFERENCES course(Id)
);




