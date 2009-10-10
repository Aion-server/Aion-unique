DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `activated` boolean NOT NULL DEFAULT FALSE, 
  `access_level` tinyint(3) NOT NULL default '0',
  `last_server` tinyint(3) NOT NULL default '-1',
  `last_ip` varchar(20) default NULL,
  `ip_force` varchar(20) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
