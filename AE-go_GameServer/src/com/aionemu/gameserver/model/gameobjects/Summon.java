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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.SummonGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.SummonLifeStats;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 * 
 */
public class Summon extends Creature
{

	private Player	master;
	private SummonMode mode;
	private byte level;

	public static enum SummonMode
	{
		ATTACK(0),
		GUARD(1),
		REST(2),
		RELEASE(3);
		
		private int id;
		
		private SummonMode(int id)
		{
			this.id = id;
		}

		/**
		 * @return the id
		 */
		public int getId()
		{
			return id;
		}	
	}
	/**
	 * 
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param statsTemplate 
	 * @param position
	 */
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate,
		VisibleObjectTemplate objectTemplate, SummonStatsTemplate statsTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());

		controller.setOwner(this);
		super.setGameStats(new SummonGameStats(this, statsTemplate));
		super.setLifeStats(new SummonLifeStats(this));
		
		this.mode = SummonMode.GUARD;
	}

	/**
	 * @return the owner
	 */
	@Override
	public Player getMaster()
	{
		return master;
	}

	/**
	 * @param master
	 *            the master to set
	 */
	public void setMaster(Player master)
	{
		this.master = master;
	}

	@Override
	public String getName()
	{
		return objectTemplate.getName();
	}

	/**
	 * @return the level
	 */
	@Override
	public byte getLevel()
	{
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(byte level)
	{
		this.level = level;
	}

	@Override
	public void initializeAi()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public NpcTemplate getObjectTemplate()
	{
		return (NpcTemplate) super.getObjectTemplate();
	}

	public int getNpcId()
	{
		return getObjectTemplate().getTemplateId();
	}
	
	public int getNameId()
	{
		return getObjectTemplate().getNameId();
	}

	/**
	 * @return NpcObjectType.SUMMON
	 */
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.SUMMON;
	}

	@Override
	public SummonController getController()
	{
		return (SummonController) super.getController();
	}

	/**
	 * @return the mode
	 */
	public SummonMode getMode()
	{
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(SummonMode mode)
	{
		this.mode = mode;
	}

	@Override
	protected boolean isEnemyNpc(Npc visibleObject)
	{
		return master.isEnemyNpc(visibleObject);
	}

	@Override
	protected boolean isEnemyPlayer(Player visibleObject)
	{
		return master.isEnemyPlayer(visibleObject);
	}

	@Override
	protected boolean isEnemySummon(Summon summon)
	{
		return master.isEnemySummon(summon);
	}
}
