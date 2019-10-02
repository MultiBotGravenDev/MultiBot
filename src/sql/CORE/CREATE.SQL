-- TODO: Check if backticks are required by MariaDB

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
CREATE TABLE `anti_roles` 
(
    `user_id` CHAR(50) NOT NULL,
    `role` CHAR(20) NOT NULL,
    `start` DATETIME NOT NULL,
    CONSTRAINT UK_ANT_ROL 
        UNIQUE KEY `anti_roles_pk` (`user_id`, `role`)
) ENGINE=InnoDB;

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
CREATE TABLE `bad_words` 
(
    `word` varchar(20) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE `custom_commands` 
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `command` varchar(50) NOT NULL,
    `text` text NOT NULL,
    CONSTRAINT PK_CUSTOM
        PRIMARY KEY (`id`),
    CONSTRAINT UK_CUSTOM_COMM
        UNIQUE KEY `custom_commands_command_uindex` (`command`)
) ENGINE=InnoDB;

CREATE TABLE `experience` 
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `discord_id` char(50) NOT NULL,
    `experience` int(11) NOT NULL DEFAULT '0',
    `level` int(11) NOT NULL DEFAULT '0',
    `messages_count` int(11) NOT NULL DEFAULT '0',
    `last_message` datetime DEFAULT NULL,
    CONSTRAINT PK_EXP
        PRIMARY KEY (`id`),
    CONSTRAINT UK_EXP_DID
        UNIQUE KEY `discord_id` (`discord_id`)
) ENGINE=InnoDB;

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
-- TODO: needs to rename id to guild_id and to add an id (PK)
CREATE TABLE `guild_id` 
(
    `name` varchar(20) NOT NULL,
    `id` char(18) NOT NULL,
    CONSTRAINT UK_GUILD_NAME
        UNIQUE KEY `guild_id_name_uindex` (`name`)
) ENGINE=InnoDB;

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
-- TODO: needs to rename id to immunised_id and to add an id (PK)
CREATE TABLE `immunised_ids` 
(
    `id` char(18) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE `infractions` 
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(36) DEFAULT NULL,
    `punished_id` char(50) NOT NULL,
    `punisher_id` char(50) NOT NULL,
    `type` enum('BAN','MUTE','KICK','WARN') NOT NULL,
    `reason` varchar(255) NOT NULL,
    `start` datetime NOT NULL,
    `end` datetime DEFAULT NULL,
    `finished` tinyint(1) NOT NULL,
    CONSTRAINT PK_INFRA
        PRIMARY KEY (`id`),
    CONSTRAINT UK_INFRA_UUID
        UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB;

CREATE TABLE `logs` 
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `discord_id` char(50) NOT NULL,
    `message_id` char(50) NOT NULL,
    `content` text NOT NULL,
    `creation` datetime NOT NULL,
    CONSTRAINT PK_LOGS
        PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
-- TODO: needs to rename id to ???_id (what's that??) and to add an id (PK)
CREATE TABLE `quiz_messages` 
(
    `id` char(5) NOT NULL,
    `text` text NOT NULL,
    CONSTRAINT UK_QUIZ_ID
        UNIQUE KEY `quiz_messages_id_uindex` (`id`)
) ENGINE=InnoDB;

-- TODO: bad thing: needs to set a primary key (an id auto_increment)
CREATE TABLE `roles` 
(
    `role_id` char(18) NOT NULL,
    `emote_id` text NOT NULL,
    CONSTRAINT UK_ROLES_RID
        UNIQUE KEY `roles_role_id_uindex` (`role_id`)
) ENGINE=InnoDB;

-- TODO: check if id can be a foreign key!
CREATE TABLE `voters` (
    `id` int(11) NOT NULL,
    `voter_id` char(50) NOT NULL,
    `choice` char(5) NOT NULL,
    CONSTRAINT PK_VOTERS
        PRIMARY KEY (`id`,`voter_id`)
) ENGINE=InnoDB;

CREATE TABLE `votes` (
    `id` int(4) NOT NULL AUTO_INCREMENT,
    `message_id` char(50) NOT NULL,
    `role` char(20) NOT NULL,
    `user_id` char(50) NOT NULL,
    `accepted` tinyint(1) NOT NULL,
    CONSTRAINT PK_VOTES
        PRIMARY KEY (`id`),
    CONSTRAINT UK_VOTES_MID
        UNIQUE KEY `votes_message_id_uindex` (`message_id`)
) ENGINE=InnoDB;

-- comment: Messages sent in lisez-ce-salon
-- TODO: remove COMMENT??
-- TODO: bad thing: needs to set a primary key (an id auto_increment)
CREATE TABLE `welcome_messages` (
    `id` int(11) NOT NULL,
    `text` text NOT NULL,
    CONSTRAINT UK_WM_ID
        UNIQUE KEY `welcome_messages_id_uindex` (`id`)
) ENGINE=InnoDB COMMENT='messages sent in lisez-ce-salon';
