ALTER TABLE `legions` ADD COLUMN `disband_time` int(11) NOT NULL default '0' AFTER `centurion_permission2`;
ALTER TABLE `legion_announcement_list` CHANGE date date int(11) NULL default '0';
ALTER TABLE `legion_announcement_list` ADD COLUMN `temp_date` timestamp NOT NULL default '0000-00-00 00:00:00' AFTER `date`;
UPDATE `legion_announcement_list` set temp_date=FROM_UNIXTIME(date);
UPDATE `legion_announcement_list` set date=NULL;
ALTER TABLE `legion_announcement_list` CHANGE date date timestamp NOT NULL default CURRENT_TIMESTAMP;
UPDATE `legion_announcement_list` set date=temp_date;
ALTER TABLE `legion_announcement_list` DROP `temp_date`;