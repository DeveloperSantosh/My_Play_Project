--liquibase formatted sql


--changeset santosh:create-multiple-tables splitStatements:true endDelimiter:;
--comment: Created all tables
CREATE TABLE IF NOT EXISTS MY_BLOGS(
                BLOG_ID INTEGER AUTO_INCREMENT,
                BLOG_TITLE varchar(200) NOT NULL,
                BLOG_CONTENT text NOT NULL,
                CREATION_TIME varchar(200) NOT NULL,
                AUTHOR_ID INTEGER NOT NULL,
                PRIMARY KEY (BLOG_ID),
                FOREIGN KEY (AUTHOR_ID) REFERENCES MY_USER(USER_ID));

CREATE TABLE IF NOT EXISTS BLOG_COMMENTS(
                COMMENT_ID INTEGER AUTO_INCREMENT,
                COMMENT varchar(200) NOT NULL,
                CREATION_TIME varchar(200) NOT NULL,
                BLOG_ID INTEGER NOT NULL,
                PRIMARY KEY (COMMENT_ID),
                FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID));

CREATE TABLE IF NOT EXISTS MY_IMAGES(
                IMAGE_ID INTEGER AUTO_INCREMENT,
                IMAGE_PATH varchar(200) NOT NULL,
                BLOG_ID INTEGER NOT NULL,
                PRIMARY KEY (IMAGE_ID),
                FOREIGN KEY (BLOG_ID) REFERENCES MY_BLOGS(BLOG_ID));

CREATE TABLE IF NOT EXISTS MY_PERMISSIONS(
                PERMISSION_ID INTEGER AUTO_INCREMENT,
                VALUE varchar(200) NOT NULL,
                PRIMARY KEY (PERMISSION_ID));

CREATE TABLE IF NOT EXISTS MY_ROLE(
                ROLE_TYPE varchar(200),
                ROLE_DESCRIPTION varchar(200) NOT NULL,
                PRIMARY KEY (ROLE_TYPE));

CREATE TABLE IF NOT EXISTS MY_USER_PERMISSIONS(
                PERMISSION_ID INTEGER NOT NULL,
                USER_ID INTEGER NOT NULL,
                FOREIGN KEY (PERMISSION_ID) REFERENCES MY_PERMISSIONS (PERMISSION_ID),
                FOREIGN KEY (USER_ID) REFERENCES MY_USER (USER_ID),
                PRIMARY KEY (PERMISSION_ID, USER_ID));

CREATE TABLE IF NOT EXISTS MY_USER(
                USER_ID INTEGER AUTO_INCREMENT,
                USERNAME varchar(200) NOT NULL,
                PASSWORD varchar(200) NOT NULL,
                EMAIL varchar(200) UNIQUE,
                PRIMARY KEY (USER_ID));

CREATE TABLE IF NOT EXISTS MY_USER_ROLE(
                ROLE_TYPE varchar(200) NOT NULL,
                USER_ID INTEGER NOT NULL,
                FOREIGN KEY (ROLE_TYPE) REFERENCES MY_ROLE(ROLE_TYPE),
                FOREIGN KEY (USER_ID) REFERENCES MY_USER(USER_ID),
                PRIMARY KEY (ROLE_TYPE, USER_ID));

--changeset santosh:add-user
--comment: Added user
INSERT INTO MY_USER (USERNAME, PASSWORD, EMAIL) VALUES("ANYONE","ANYTHING","anyone@anything.com");
