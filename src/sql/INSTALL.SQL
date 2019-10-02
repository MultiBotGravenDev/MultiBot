-- TODO: Check if backticks are required by MariaDB

/*
    It has no semi-colon at the end of non SQL queries
    USE, SOURCE, etc. are DBMS console queries/commands
*/
DROP DATABASE IF EXISTS `multibot`;
CREATE DATABASE `multibot`;

USE `multibot`

SOURCE CORE/UTF8.SQL
SOURCE CORE/CREATE.SQL
