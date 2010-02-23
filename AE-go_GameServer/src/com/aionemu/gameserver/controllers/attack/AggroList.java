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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer, KKnD
 *
 */
public class AggroList
{
	private Npc owner;
	
	public AggroList(Npc owner)
	{
		super();
		this.owner = owner;
	}

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
	
	private FastMap<Creature, AggroInfo> aggroList = new FastMap<Creature, AggroInfo>().setShared(true);

	/**
	 * @return the owner
	 */
	public Npc getOwner()
	{
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Npc owner)
	{
		this.owner = owner;
	}

	/**
	 * 
	 * @param attacker
	 * @param damage
	 * @param aggro
	 */
	public void addDamageHate(Creature attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;

		AggroInfo ai = aggroList.get(attacker);
		if (ai == null)
		{
			ai = new AggroInfo(attacker);
			aggroList.put(attacker, ai);

			ai.damage = 0;
			ai.hate = 0;
		}
		ai.damage += damage;

		if (aggro == 0)
			ai.hate++;
		else
			ai.hate += aggro;
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
	 */
	public void stopHating(Creature creature)
	{
		AggroInfo aggroInfo = aggroList.get(creature);
		if(aggroInfo != null)
			aggroInfo.hate = 0;
	}
	
	public void remove(Creature creature)
	{
		aggroList.remove(creature);
	}
	
	public void clear()
	{
		aggroList.clear();
	}
}
