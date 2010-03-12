CREATE TABLE IF NOT EXISTS  `mail` (
`mailUniqueId` int(11) NOT NULL, `mailRecipientId` int(11) NOT NULL,
`senderName` varchar(16) character set utf8 NOT NULL,`mailTitle` varchar(20) character set utf8 NOT NULL,
`mailMessage` varchar(500) character set utf8 NOT NULL, `unread` tinyint(4) NOT NULL default '1',
`attachedItemId` int(11) NOT NULL, `attachedKinahCount` int(11) NOT NULL,
`express` tinyint(4) NOT NULL default '0', `recievedTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
PRIMARY KEY  (`mailUniqueId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
							
ALTER TABLE `players` ADD `mailboxLetters` TINYINT DEFAULT '0' NOT NULL AFTER `warehouse_size` ;