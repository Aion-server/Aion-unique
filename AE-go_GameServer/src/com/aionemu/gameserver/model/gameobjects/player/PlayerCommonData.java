/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class is holding base information about player, that may be used even when player itself is not online.
 * 
 * @author Luno
 * 
 */
public class PlayerCommonData
{
	/** Logger used by this class and {@link StaticData} class */
	static Logger			log	= Logger.getLogger(PlayerCommonData.class);
	
	private final int		playerObjId;
	private Race			race;
	private String			name;
	private PlayerClass		playerClass;
	/** Should be changed right after character creation **/
	private int				level = 0;
	private long			exp = 0;
	private long			expLoss = 0;
	private boolean			admin;
	private Gender			gender;
	private Timestamp		lastOnline;
	private boolean 		online;
	private String 			note;
	private WorldPosition	position;

	

	public PlayerCommonData(int objId)
	{
		this.playerObjId = objId;
	}

	public int getPlayerObjId()
	{
		return playerObjId;
	}
	
	public long getExp()
	{
		return this.exp;
	}
	
	public long getExpShown()
	{
		return this.exp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
	}
	
	public long getExpNeed()
	{
		if (this.level == DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
		{
			return 0;
		}
		return DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level+1);
	}

	public void setExpLoss()
	{
		int calculatedExpLoss = Math.round(getExpNeed() / 100 * 3);
		expLoss = expLoss + calculatedExpLoss;
		if (getExpShown() < expLoss) {
			this.expLoss = getExpShown();
		}
		this.expLoss = expLoss;
		PacketSendUtility.sendPacket(this.getPlayer(), new SM_STATS_INFO(this.getPlayer()));
	}

	public void restoreRecoverableExp(long exp)
	{
		this.expLoss = expLoss - exp;
	}

	public long getExpRecoverable()
	{
		return expLoss;
	}

	public void setExp(long exp)
	{


		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);

		if (exp > maxExp)
		{
			exp = maxExp;
		}

		int level = 1;
		long totalExp = 0;
		long leakExp = 0;
		


		while (exp>= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level+1)+DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level) && level != maxLevel)
		{

			level++;
			totalExp = leakExp + DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level);
		}

		if (level>1) {
			leakExp = exp - totalExp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level-1);
		} 
		else {
			leakExp = exp - totalExp;
		}

		if (level > this.level)
		{
			this.level = level;
			this.exp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level) + leakExp;
			
			if(this.getPlayer()!= null)
			{
				Player player = this.getPlayer();
				PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
				
				player.setLifeStats(new PlayerLifeStats(statsTemplate.getMaxHp(), statsTemplate.getMaxMp()));
				player.setGameStats(new PlayerGameStats(DataManager.PLAYER_STATS_DATA,player));
				player.setPlayerStatsTemplate(statsTemplate);
				
				PacketSendUtility.sendPacket(this.getPlayer(),
					new SM_LEVEL_UPDATE(this.getPlayerObjId(), level));
				
				PacketSendUtility.sendPacket(this.getPlayer(), new SM_STATS_INFO(this.getPlayer()));
			}


		}
		else
		{
			this.exp = exp;
			
			if(this.getPlayer()!=null)
			{
				PacketSendUtility.sendPacket(this.getPlayer(),
					new SM_STATUPDATE_EXP(this.getExpShown(), this.getExpRecoverable(), this.getExpNeed()));
			}
		}
	}

	public Race getRace()
	{
		return race;
	}

	public void setRace(Race race)
	{
		this.race = race;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}

	public boolean isOnline() 
	{
		return online;
	}
	public void setOnline(boolean online)
	{
		this.online = online;
	}
	public Gender getGender()
	{
		return gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public WorldPosition getPosition()
	{
		return position;
	}
	
	public Timestamp getLastOnline()
	{
		return lastOnline;
	}
	
	public void setLastOnline(Timestamp timestamp)
	{
		lastOnline = timestamp;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		if (level <= DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
		{
			this.setExp(DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level));
		}
	}
	
	public String getNote()
	{
		return note;
	}
	
	public void setNote(String note)
	{
		this.note = note;
	}

	/**
	 * This method should be called exactly once after creating object of this class
	 * @param position
	 */
	public void setPosition(WorldPosition position)
	{
		if(this.position != null)
		{
			throw new IllegalStateException("position already set");
		}
		this.position = position;
	}
	
	/**
	 * Gets the cooresponding Player for this common data.
	 * Returns null if the player is not online
	 * @return Player or null
	 */
	public Player getPlayer()
	{
		if (online && getPosition() != null)
		{
			return getPosition().getWorld().findPlayer(playerObjId);
		}
		return null;
	}
}
