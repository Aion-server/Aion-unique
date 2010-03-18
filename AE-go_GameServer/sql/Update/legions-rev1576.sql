CREATE TABLE IF NOT EXISTS `legion_history` (
  `legion_id` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `history_type` enum( 'CREATE', 'JOIN', 'KICK', 'APPOINTED', 'EMBLEM_REGISTER', 'EMBLEM_MODIFIED' ) NOT NULL,
  `name` VARCHAR( 16 ) NOT NULL,
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;