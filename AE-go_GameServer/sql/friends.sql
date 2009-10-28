DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends` (
  `player` int(11) NOT NULL,
  `friend` int(11) NOT NULL,
  PRIMARY KEY (`player`,`friend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;