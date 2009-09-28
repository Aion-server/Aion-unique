SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for blocks
-- ----------------------------

CREATE TABLE `blocks` (
  `player` int(11) NOT NULL,
  `blocked_player` int(11) NOT NULL,
  `reason` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`player`,`blocked_player`),
  FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`blocked_player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;