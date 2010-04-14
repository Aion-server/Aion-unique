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
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 * 
 */
public class Summon extends Creature
{

	private Player	master;

	/**
	 * 
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param position
	 */
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate,
		VisibleObjectTemplate objectTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());

		controller.setOwner(this);

		super.setGameStats(new SummonGameStats(this));
		super.setLifeStats(new SummonLifeStats(this));
	}

	/**
	 * @return the owner
	 */
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

	@Override
	public byte getLevel()
	{
		// TODO Auto-generated method stub
		return 1;
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

}
