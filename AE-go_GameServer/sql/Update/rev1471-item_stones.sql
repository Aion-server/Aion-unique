ALTER TABLE `item_stones` ADD COLUMN `category` int(2) NOT NULL DEFAULT '0' after `slot`;
ALTER TABLE `item_stones` DROP PRIMARY KEY, ADD PRIMARY KEY(`itemUniqueId`, `slot`, `category`);