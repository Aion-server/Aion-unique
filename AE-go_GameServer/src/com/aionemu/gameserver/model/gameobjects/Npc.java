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

import java.util.Map.Entry;

import com.aionemu.gameserver.ai.npcai.NpcAi;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.gameobjects.stats.NpcGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
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
	
	/**
	 *  Template keeping all base data for this npc 
	 */
	protected NpcTemplate		template;


	/**
	 * Constructor creating instance of Npc.
	 * 
	 * @param spawn
	 *            SpawnTemplate which is used to spawn this npc
	 * @param objId
	 *            unique objId
	 */
	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate)
	{
		super(objId, controller, spawnTemplate, new WorldPosition());
		controller.setOwner(this);
		
		this.template = (NpcTemplate) spawnTemplate.getObjectTemplate();
		NpcStatsTemplate nst = template.getStatsTemplate();
		super.setGameStats(new NpcGameStats(this,nst));
		NpcEquippedGear gear = template.getEquipment();
		if (gear!=null) 
		{
			for (Entry<ItemSlot,ItemTemplate> it : gear)
			{
				ItemEquipmentListener.onItemEquipment(it.getValue(), it.getKey().getSlotIdMask(), getGameStats());
			}
		}
	}

	public NpcTemplate getTemplate()
	{
		return template;
	}
	@Override
	public String getName()
	{
		return getTemplate().getName();
	}

	public int getNpcId()
	{
		return getTemplate().getTemplateId();
	}

	@Override
	public byte getLevel()
	{
		return getTemplate().getLevel();
	}

	/**
	 * @return the lifeStats
	 */
	public NpcLifeStats getLifeStats()
	{
		return (NpcLifeStats) super.getLifeStats();
	}

	/**
	 * @return the gameStats
	 */
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
	public void initializeAi()
	{
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasWalkRoutes()
	{
		return getSpawn().getWalkerId() > 0;
	}
	
	public boolean isAggressive()
	{
			return ((NpcTemplate)this.getSpawn().getObjectTemplate()).getNpcType() == NpcType.AGGRESSIVE;
	}
	
	public int getAggroRange()
	{
			return ((NpcTemplate)this.getSpawn().getObjectTemplate()).getAggroRange();
	}
}
