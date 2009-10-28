DROP TABLE IF EXISTS `player_skills`;
CREATE TABLE `player_skills` (
  `player_id` int(11) NOT NULL default '0',
  `skillId` int(11) NOT NULL default '0',
  `skillLevel` int(11) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`skillId`,`skillLevel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;