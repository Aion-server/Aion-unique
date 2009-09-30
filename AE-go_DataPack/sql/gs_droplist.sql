/*
MySQL Data Transfer
Source Host: localhost
Source Database: aiondb
Target Host: localhost
Target Database: aiondb
Date: 9/29/2009 2:37:16 PM
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for droplist
-- ----------------------------
CREATE TABLE `droplist` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `mobId` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `min` int(11) NOT NULL DEFAULT '0',
  `max` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=2848 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
