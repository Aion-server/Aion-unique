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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai.npcai.NpcAi;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.stats.NpcGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class is a base class for all in-game NPCs, what includes: monsters and npcs that player can talk to (aka
 * Citizens)
 * 
 * @author Luno
 * 
 */
public class Npc extends Creature
{

	private AggroList aggroList;
	
	/**
	 * Constructor creating instance of Npc.
	 * 
	 * @param spawn
	 *            SpawnTemplate which is used to spawn this npc
	 * @param objId
	 *            unique objId
	 */
	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());
		controller.setOwner(this);
		
		NpcStatsTemplate nst = getObjectTemplate().getStatsTemplate();
		super.setGameStats(new NpcGameStats(this,nst));
		
		this.aggroList = new AggroList(this);
	}

	public NpcTemplate getObjectTemplate()
	{
		return (NpcTemplate) objectTemplate;
	}
	@Override
	public String getName()
	{
		return getObjectTemplate().getName();
	}

	public int getNpcId()
	{
		return getObjectTemplate().getTemplateId();
	}

	@Override
	public byte getLevel()
	{
		return getObjectTemplate().getLevel();
	}

	/**
	 * @return the lifeStats
	 */
	@Override
	public NpcLifeStats getLifeStats()
	{
		return (NpcLifeStats) super.getLifeStats();
	}

	/**
	 * @return the gameStats
	 */
	@Override
	public NpcGameStats getGameStats()
	{
		return (NpcGameStats) super.getGameStats();
	}
	
	@Override
	public NpcAi getAi()
	{
		return (NpcAi) super.getAi();
	}
		
	@Override
	public NpcController getController()
	{
		return (NpcController) super.getController();
	}

	public boolean hasWalkRoutes()
	{
		return getSpawn().getWalkerId() > 0;
	}
	
	public boolean isAggressive()
	{
		return isAggressiveTo(Race.ELYOS) || isAggressiveTo(Race.ASMODIANS);
	}
	/**
	 *  //TODO refactore to npc-npc interations
	 *  
	 * @param tribe
	 * @return true or false
	 */
	public boolean isAggressiveTo(Race race)
	{
		String currentTribe = getObjectTemplate().getTribe();
		switch(race)
		{
			case ELYOS:
				return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(currentTribe, "PC");
			case ASMODIANS:
				return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(currentTribe, "PC_DARK");
		}
		return false;
	}
	
	public int getAggroRange()
	{
		return getObjectTemplate().getAggroRange();
	}
	
	@Override
	public void initializeAi()
	{

	}

	/**
	 *  Check whether npc located at initial spawn location
	 *  
	 * @return true or false
	 */
	public boolean isAtSpawnLocation()
	{
		return MathUtil.getDistance(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(),
			getX(), getY(), getZ()) < 3 ;
	}
	
	/**
	 * @return the aggroList
	 */
	public AggroList getAggroList()
	{
		return aggroList;
	}
}
