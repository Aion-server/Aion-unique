DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
`itemUniqueId` BIGINT NOT NULL,
`itemId` INT NOT NULL DEFAULT '0',
`itemCount` INT NOT NULL DEFAULT '0',
`itemOwner` INT NOT NULL DEFAULT '0',
`isEquiped` TINYINT(1) NOT NULL DEFAULT '0',
`slot` INT NOT NULL DEFAULT '0',
PRIMARY KEY ( `itemUniqueId` ),
FOREIGN KEY ( `itemOwner` ) references players ( `id` )
) ENGINE=MyISAM DEFAULT CHARSET=latin1;