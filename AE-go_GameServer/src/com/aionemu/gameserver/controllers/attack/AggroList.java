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
package com.aionemu.gameserver.controllers.attack;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer, KKnD
 *
 */
public class AggroList
{	
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(AggroList.class);
	
	private Npc owner;
	
	private FastMap<Creature, AggroInfo> aggroList = new FastMap<Creature, AggroInfo>().shared();
	
	public AggroList(Npc owner)
	{
		this.owner = owner;
	}

	/**
	 *  AggroInfo:
	 *  - hate of creature
	 *  - damage of creature
	 */
	public final class AggroInfo
	{
		protected Creature attacker;
		protected int hate;
		protected int damage;

		AggroInfo(Creature pAttacker)
		{
			attacker = pAttacker;
		}
	}

	/**
	 * 
	 * @param creature
	 * @param damage
	 */
	public void addDamage(Creature creature, int damage)
	{
		if (creature == null)
			return;

		AggroInfo ai = getAggroInfo(creature);
		ai.damage += damage;
		/**
		 * For now we add hate equal to each damage received
		 * Additionally there will be broadcast of extra hate
		 */
		ai.hate += damage;
		
		if(owner.getAi() != null)
			owner.getAi().handleEvent(Event.ATTACKED);
	}

	/**
	 * Extra hate that is received from using non-damange skill effects
	 * 
	 * @param creature
	 * @param hate
	 */
	public void addHate(Creature creature, int hate)
	{
		if (creature == null)
			return;

		AggroInfo ai = getAggroInfo(creature);
		ai.hate += hate;
		
		if(owner.getAi() != null)
			owner.getAi().handleEvent(Event.ATTACKED);
	}
	
	/**
	 * 
	 * @return most hated creature
	 */
	public Creature getMostHated()
	{
		if (aggroList.isEmpty())
			return null;

		Creature mostHated = null;
		int maxHate = 0;

		for (AggroInfo ai : aggroList.values())
		{
			if (ai == null)
				continue;

			if(ai.attacker.getLifeStats().isAlreadyDead()
				|| !owner.getKnownList().knowns(ai.attacker))
				ai.hate = 0;

			if (ai.hate > maxHate)
			{
				mostHated = ai.attacker;
				maxHate = ai.hate;
			}
		}

		return mostHated;
	}	
	
	/**
	 * 
	 * @param creature
	 * @return
	 */
	public boolean isMostHated(Creature creature)
	{
		if(creature == null || creature.getLifeStats().isAlreadyDead())
			return false;
		
		Creature mostHated = getMostHated();
		if(mostHated == null)
			return false;
		
		return mostHated.equals(creature);
	}

	/**
	 * @param creature
	 * @param value
	 */
	public void notifyHate(Creature creature, int value)
	{
		if(isHating(creature))
			addHate(creature, value);
	}
	
	/**
	 * 
	 * @param creature
	 */
	public void stopHating(Creature creature)
	{
		AggroInfo aggroInfo = aggroList.get(creature);
		if(aggroInfo != null)
			aggroInfo.hate = 0;
	}
	
	/**
	 * Remove completely creature from aggro list
	 * 
	 * @param creature
	 */
	public void remove(Creature creature)
	{
		aggroList.remove(creature);
	}
	
	/**
	 * Clear aggroList
	 */
	public void clear()
	{
		aggroList.clear();
	}
	
	/**
	 * 
	 * @param creature
	 * @return
	 */
	private AggroInfo getAggroInfo(Creature creature)
	{
		AggroInfo ai = aggroList.get(creature);
		if (ai == null)
		{
			ai = new AggroInfo(creature);
			aggroList.put(creature, ai);
		}
		return ai;
	}
	
	/**
	 * 
	 * @param creature
	 * @return
	 */
	private boolean isHating(Creature creature)
	{
		return aggroList.containsKey(creature);
	}

}
