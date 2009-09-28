SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for friends
-- ----------------------------
CREATE TABLE `friends` (
  `player` int(11) NOT NULL,
  `friend` int(11) NOT NULL,
  PRIMARY KEY (`player`,`friend`),
  FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`friend`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;