/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.condition.ConditionChangeListener;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class Env
{
	private World world;

	private Creature effected;

	private Creature effector;

	private SkillTemplate skillTemplate;
	
	private int skillLevel;

	private ConditionChangeListener conditionChangeListener;

	public Env(Player player, SkillTemplate skillTemplate, 
		ConditionChangeListener conditionChangeListener, World world)
	{
		super();
		this.effected = (Creature) player.getTarget();
		this.effector = player;
		this.skillTemplate = skillTemplate;
		this.conditionChangeListener = conditionChangeListener;
		this.skillLevel = player.getSkillList().getSkillLevel(skillTemplate.getSkillId());
		this.world = world;
	}

	/**
	 * @return the selectedTarget
	 */
	public Creature getEffected()
	{
		return effected;
	}

	/**
	 * @return the skillUser
	 */
	public Creature getEffector()
	{
		return effector;
	}

	/**
	 * @param effected the effected to set
	 */
	public void setEffected(Creature effected)
	{
		this.effected = effected;
	}

	/**
	 * @param effector the effector to set
	 */
	public void setEffector(Creature effector)
	{
		this.effector = effector;
	}

	/**
	 * @return the skillTemplate
	 */
	public SkillTemplate getSkillTemplate()
	{
		return skillTemplate;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}

	/**
	 * @return the conditionChangeListener
	 */
	public ConditionChangeListener getConditionChangeListener()
	{
		return conditionChangeListener;
	}

	/**
	 * @param world the world to set
	 */
	public World getWorld()
	{
		return world;
	}
}
