-- ----------------------------
-- Table structure for inventory
-- ----------------------------
CREATE TABLE `inventory` (
`itemUniqueId` BIGINT NOT NULL AUTO_INCREMENT ,
`itemId` INT NOT NULL DEFAULT '0',
`itemNameId` INT NOT NULL DEFAULT '0',
`itemCount` INT NOT NULL DEFAULT '0',
`itemOwner` INT NOT NULL DEFAULT '0',
PRIMARY KEY ( `itemUniqueId` )
) ENGINE=MyISAM DEFAULT CHARSET=latin1;