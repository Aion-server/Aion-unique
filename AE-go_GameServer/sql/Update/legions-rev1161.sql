ALTER TABLE `legion_members` ADD COLUMN `temp_rank` ENUM( 'BRIGADE_GENERAL', 'CENTURION', 'LEGIONARY' ) NOT NULL DEFAULT 'LEGIONARY' AFTER `rank`;

ALTER TABLE `legion_members` CHANGE rank rank int(11) NULL default '2';
UPDATE `legion_members` set temp_rank='CENTURION' where rank=1;
UPDATE `legion_members` set temp_rank='LEGIONARY' where rank=2;
UPDATE `legion_members` set temp_rank='BRIGADE_GENERAL' where rank=0;
UPDATE `legion_members` set rank=NULL;
ALTER TABLE `legion_members` CHANGE `rank` `rank` ENUM( 'BRIGADE_GENERAL', 'CENTURION', 'LEGIONARY' ) NOT NULL DEFAULT 'LEGIONARY';
UPDATE `legion_members` set rank=temp_rank;
ALTER TABLE `legion_members` DROP `temp_rank`;