SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for gameservers
-- ----------------------------
CREATE TABLE `gameservers` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;