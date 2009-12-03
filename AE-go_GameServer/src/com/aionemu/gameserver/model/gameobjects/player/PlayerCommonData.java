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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.skillengine.SkillLearnService;
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
	private int 			cubeSize = 0;
	private int			    bindPoint;
   private int             titleId;
	

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
	public int getCubeSize()
	{
		return this.cubeSize;
	}
	public void setCubesize(int cubeSize)
	{
		this.cubeSize = cubeSize;
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
		return DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level+1) - 
			DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
	}

	public void setExpLoss()
	{
		int calculatedExpLoss = Math.round(getExpNeed() / 100 * 3);
		expLoss = expLoss + calculatedExpLoss;
		if (getExpShown() < expLoss) {
			this.expLoss = getExpShown();
		}
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
	
	//TODO need to test before use
	public void addExp(long value)
	{
		long newExp = this.exp + value;
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);

		if (newExp > maxExp)
		{
			newExp = maxExp;
		}
		
		this.exp = newExp;
		
		if(this.level != maxLevel)
		{
			long nextLevelExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level + 1);
			if(nextLevelExp < newExp)
			{
				
				this.level += 1;
				upgradePlayer();
			}
			else
			{			
				if(this.getPlayer()!=null)
				{
					PacketSendUtility.sendPacket(this.getPlayer(),
						new SM_STATUPDATE_EXP(this.getExpShown(), this.getExpRecoverable(), this.getExpNeed()));
				}
			}
		}
	}

	public void setExp(long exp)
	{
		//maxLevel is 51 but in game 50 should be shown with full XP bar
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);

		if (exp > maxExp)
		{
			exp = maxExp;
		}

		int level = 1;
		long totalExp = 0;
		long leakExp = 0;
		
		//make sure level is never larget than maxLevel-1
		while ((level + 1) != maxLevel && exp>= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level+1))
		{
			level++;
			//totalExp = leakExp + DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level);
		}
		//TODO fix leakExp
		
//		if (level>1) {
//			leakExp = exp - totalExp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level-1);
//		} 
//		else {
//			leakExp = exp - totalExp;
//		}

		if (level > this.level)
		{
			this.level = level;
			Player player = getPlayer();
			if(player == null)
			{
				this.exp = exp;
			}
			else
			{
				this.exp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level);// + leakExp;
				upgradePlayer();
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

	public void upgradePlayer()
	{
		Player player = this.getPlayer();
		if(player != null)
		{
			PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
			
			player.getGameStats().doLevelUpgrade(DataManager.PLAYER_STATS_DATA, level);
			player.setPlayerStatsTemplate(statsTemplate);
			player.setLifeStats(new PlayerLifeStats(player, statsTemplate.getMaxHp(), statsTemplate.getMaxMp()));		
			player.getLifeStats().synchronizeWithMaxStats();
			
			PacketSendUtility.sendPacket(player,
				new SM_LEVEL_UPDATE(player.getObjectId(), this.level));
			
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			//add new skills
			SkillLearnService.addNewSkills(player, false);
			
			//save player at this point
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
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

	public void setBindPoint(int bindId)
	{
		this.bindPoint = bindId;
	}
	
	public int getBindPoint()
	{
		return bindPoint;
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

    public int getTitleId()
    {
        return titleId;
    }

    public void setTitleId(int titleId)
    {
        this.titleId = titleId;
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
