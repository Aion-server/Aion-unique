DROP TABLE IF EXISTS `banned_ip`;
CREATE TABLE `banned_ip` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;