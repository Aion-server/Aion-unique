SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for server_variables
-- ----------------------------
CREATE TABLE `server_variables` (
  `key` varchar(30) NOT NULL,
  `value` varchar(30) NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;