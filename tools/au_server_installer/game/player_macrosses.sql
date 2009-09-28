SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for player_macrosses
-- ----------------------------
CREATE TABLE `player_macrosses` (
  `player_id` int(8) NOT NULL,
  `order` int(3) NOT NULL,
  `macro` text NOT NULL,
  UNIQUE KEY `main` (`player_id`,`order`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;