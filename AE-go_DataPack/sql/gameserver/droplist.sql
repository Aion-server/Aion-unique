DROP TABLE IF EXISTS `droplist`;
CREATE TABLE `droplist` (

`Id` int(11) NOT NULL AUTO_INCREMENT,
`mobId` int(11) NOT NULL DEFAULT '0',
`itemId` int(11) NOT NULL DEFAULT '0',
`min` int(11) NOT NULL DEFAULT '0',
`max` int(11) NOT NULL DEFAULT '0',
`chance` int(11) NOT NULL DEFAULT '0',
PRIMARY KEY (`Id`)

) ENGINE=MyISAM DEFAULT CHARSET=utf8;


