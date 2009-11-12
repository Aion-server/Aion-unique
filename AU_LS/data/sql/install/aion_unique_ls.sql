/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50138
Source Host           : localhost:3306
Source Database       : au_server_ls

Target Server Type    : MYSQL
Target Server Version : 50138
File Encoding         : 65001

Date: 2009-11-12 22:17:14
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `account_data`
-- ----------------------------
DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `access_level` tinyint(3) NOT NULL DEFAULT '0',
  `last_server` tinyint(3) NOT NULL DEFAULT '-1',
  `last_ip` varchar(20) DEFAULT NULL,
  `ip_force` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_data
-- ----------------------------

-- ----------------------------
-- Table structure for `account_time`
-- ----------------------------
DROP TABLE IF EXISTS `account_time`;
CREATE TABLE `account_time` (
  `account_id` int(10) NOT NULL,
  `last_active` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL DEFAULT NULL,
  `session_duration` int(10) DEFAULT '0',
  `accumulated_online` int(10) DEFAULT '0',
  `accumulated_rest` int(10) DEFAULT '0',
  `penalty_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  CONSTRAINT `at_ad_fk` FOREIGN KEY (`account_id`) REFERENCES `account_data` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_time
-- ----------------------------

-- ----------------------------
-- Table structure for `banned_ip`
-- ----------------------------
DROP TABLE IF EXISTS `banned_ip`;
CREATE TABLE `banned_ip` (
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`mask`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of banned_ip
-- ----------------------------

-- ----------------------------
-- Table structure for `gameserver`
-- ----------------------------
DROP TABLE IF EXISTS `gameserver`;
CREATE TABLE `gameserver` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of gameserver
-- ----------------------------
