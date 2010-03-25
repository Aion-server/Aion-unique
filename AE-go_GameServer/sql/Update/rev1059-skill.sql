DELETE FROM player_skills where skillId = 30001 and player_id IN (SELECT `id` FROM players where exp >= 213454);
REPLACE INTO player_skills SELECT `id`, 40009, 1 FROM players where exp >= 213454;