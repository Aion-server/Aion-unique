/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.legion;

import java.sql.Timestamp;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;

/**
 * @author Simple
 * 
 */
public class LegionMemberEx extends LegionMember {
	private String name;
	private PlayerClass playerClass;
	private int level;
	private Timestamp lastOnline;
	private int worldId;
	private boolean online = false;

	/**
	 * If player is immediately after this constructor is called
	 */
	public LegionMemberEx(LegionMember legionMember, String name, PlayerClass playerClass, int level, Timestamp lastOnline, int worldId, boolean online)
	{
		super(legionMember.getObjectId(), legionMember.getLegion(), legionMember.getRank());
		this.nickname = legionMember.getNickname();
		this.selfIntro = legionMember.getSelfIntro();

		this.name = name;
		this.playerClass = playerClass;
		this.level = level;
		this.lastOnline = lastOnline;
		this.worldId = worldId;
		this.online = online;
	}

	/**
	 * If player is defined later on this constructor is called
	 */
	public LegionMemberEx(int playerObjId) {
		super(playerObjId);
	}

	/**
	 * If player is defined later on this constructor is called
	 */
	public LegionMemberEx(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public Timestamp getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Timestamp timestamp) {
		lastOnline = timestamp;
	}

	public int getLevel() {
		return level;
	}

	/**
	 * sets the exp value
	 * 
	 * @param admin
	 *            : enable decrease level
	 */
	public void setExp(long exp) {
		// maxLevel is 51 but in game 50 should be shown with full XP bar
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();

		if (getPlayerClass() != null && getPlayerClass().isStartingClass())
			maxLevel = 10;

		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE
				.getStartExpForLevel(maxLevel);
		int level = 1;

		if (exp > maxExp) {
			exp = maxExp;
		}

		// make sure level is never larger than maxLevel-1
		while ((level + 1) != maxLevel
				&& exp >= DataManager.PLAYER_EXPERIENCE_TABLE
						.getStartExpForLevel(level + 1)) {
			level++;
		}

		this.level = level;
	}

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	/**
	 * @param online
	 *            the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Checks if a LegionMemberEx is valid or not
	 * 
	 * @return true if LegionMemberEx is valid
	 */
	public boolean isValidLegionMemberEx() {
		if (getObjectId() > 0 && getName() != null && getPlayerClass() != null
				&& getLevel() > 0 && getLastOnline() != null
				&& getWorldId() > 0 && getLegion() != null && getRank() > 0
				&& getNickname() != null && getSelfIntro() != null) {
			return true;
		}
		return false;
	}
}
