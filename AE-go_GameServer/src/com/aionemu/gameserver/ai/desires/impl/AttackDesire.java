/* This file is part of aion-unique <aion-unique.com>.
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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.npcai.MonsterAi;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class indicates that character wants to attack somebody
 * 
 * @author SoulKeeper
 * @author Pinguin
 */
public final class AttackDesire extends AbstractDesire
{
	private static Logger log = Logger.getLogger(AttackDesire.class);

	/**
	 * Target of this desire
	 */
	protected Creature	target;

	/**
	 * Creates new attack desire, target can't be changed
	 * 
	 * @param crt
	 *            whom to attack
	 * @param desirePower
	 *            initial attack power
	 */
	public AttackDesire(Monster npc, int desirePower)
	{
		super(desirePower);
		this.target = npc.getController().getMostHated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleDesire(AI<?> ai)
	{
		Monster owner = ((MonsterAi)ai).getOwner();
		target = owner.getController().getMostHated();
		
		if(target == null || target.getLifeStats().isAlreadyDead())
			return;

		if(owner.getTarget() == null)
		{
			owner.setTarget(target);
		}
		
		double distance = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), target.getX(), target.getY(), target.getZ()) ;

		if(distance <= 3)
		{
			owner.getController().attackTarget(target.getObjectId());
		}
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

}