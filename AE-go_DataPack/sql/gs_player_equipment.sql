DROP TABLE IF EXISTS `player_equipment`;
CREATE TABLE `player_equipment` (
  `player_id` int(11) NOT NULL,
  `warmer` int(9) DEFAULT '0',
  `shield` int(9) DEFAULT '0',
  `helmet` int(9) DEFAULT '0',
  `armor` int(9) DEFAULT '0',
  `gloves` int(9) DEFAULT '0',
  `boots` int(9) DEFAULT '0',
  `learrings` int(9) DEFAULT '0',
  `rearrings` int(9) DEFAULT '0',
  `lring` int(9) DEFAULT '0',
  `rring` int(9) DEFAULT '0',
  `necklace` int(9) DEFAULT '0',
  `pauldron` int(9) DEFAULT '0',
  `pants` int(9) DEFAULT '0',
  `lshard` int(9) DEFAULT '0',
  `rshard` int(9) DEFAULT '0',
  `wing` int(9) DEFAULT '0',
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;