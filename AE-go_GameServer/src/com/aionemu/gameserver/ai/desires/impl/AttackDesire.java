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
package com.aionemu.gameserver.ai.desires.impl;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class indicates that character wants to attack somebody
 * 
 * @author SoulKeeper
 * @author Pinguin
 * @author ATracer
 */
public final class AttackDesire extends AbstractDesire
{
	private int attackNotPossibleCounter;
	
	/**
	 * Target of this desire
	 */
	protected Creature	target;
	
	protected Npc owner;

	/**
	 * Creates new attack desire, target can't be changed
	 * 
	 * @param npc
	 *            The Npc that's attacking
	 * @param target
	 * 			  whom to attack
	 * @param desirePower
	 *            initial attack power
	 */
	public AttackDesire(Npc npc, Creature target, int desirePower)
	{
		super(desirePower);
		this.target = target;
		this.owner = npc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handleDesire(AI<?> ai)
	{
		
		if(target == null || target.getLifeStats().isAlreadyDead())
		{
			owner.getAggroList().stopHating(target);
			owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
			return false;
		}
		
		double distance = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), target.getX(), target.getY(), target.getZ()) ;
		
		if(distance > 50)
		{
			owner.getAggroList().stopHating(target);
			owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
			return false;
		}
		
		if(distance <= 2)
		{
			owner.getController().attackTarget(target.getObjectId());
			attackNotPossibleCounter = 0;
		}
		else
		{
			attackNotPossibleCounter++;
		}
		
		if(attackNotPossibleCounter > 10)
		{
			owner.getAggroList().stopHating(target);
			owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
			return false;
		}	
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof AttackDesire))
			return false;

		AttackDesire that = (AttackDesire) o;

		return target.equals(that.target);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return target.hashCode();
	}

	/**
	 * Returns target of this desire
	 * 
	 * @return target of this desire
	 */
	public Creature getTarget()
	{
		return target;
	}

	@Override
	public int getExecutionInterval()
	{
		return 2;
	}

	@Override
	public void onClear()
	{
		
	}
	
}