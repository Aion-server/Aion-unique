-- ----------------------------
-- account_data
-- ----------------------------

DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `activated` boolean NOT NULL DEFAULT TRUE, 
  `access_level` tinyint(3) NOT NULL default '0',
  `membership` tinyint(3) NOT NULL default '0',
  `last_server` tinyint(3) NOT NULL default '-1',
  `last_ip` varchar(20) default NULL,
  `ip_force` varchar(20) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- account_time
-- ----------------------------

DROP TABLE IF EXISTS `account_time`;
CREATE TABLE `account_time` (
  `account_id` int(11) NOT NULL,
  `last_active` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL default NULL,
  `session_duration` int(10) default '0',
  `accumulated_online` int(10) default '0',
  `accumulated_rest` int(10) default '0',
  `penalty_end` timestamp NULL default NULL,
  PRIMARY KEY  (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- banned_ip
-- ----------------------------

DROP TABLE IF EXISTS `banned_ip`;
CREATE TABLE `banned_ip` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- gameservers
-- ----------------------------

DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;