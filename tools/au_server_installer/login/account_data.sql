-- ----------------------------
-- Table structure for account_data
-- ----------------------------
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `access_level` tinyint(3) NOT NULL DEFAULT '0',
  `last_server` tinyint(3) NOT NULL DEFAULT '-1',
  `last_ip` varchar(20) DEFAULT NULL,
  `ip_force` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
