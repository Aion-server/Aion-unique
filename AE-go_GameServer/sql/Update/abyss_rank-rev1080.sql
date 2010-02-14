ALTER TABLE `abyss_rank` ADD COLUMN `all_kill` int(4) NOT NULL default '0' AFTER `rank`;
ALTER TABLE `abyss_rank` ADD COLUMN `max_rank` int(4) NOT NULL default '1' AFTER `all_kill`;