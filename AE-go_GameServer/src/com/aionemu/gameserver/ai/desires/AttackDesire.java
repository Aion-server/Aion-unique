/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.ai.desires;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.task.AiTask;
import com.aionemu.gameserver.ai.task.AttackTask;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class indicates that character wants to attack somebody
 * 
 * @author SoulKeeper
 * @author Pinguin
 */
public final class AttackDesire extends AbstractDesire
{
	private static Logger log = Logger.getLogger(AttackDesire.class);

	AiTask task ;
	/**
	 * Target of this desire
	 */
	protected final Creature	target;

	/**
	 * Creates new attack desire, target can't be changed
	 * 
	 * @param target
	 *            whom to attack
	 * @param desirePower
	 *            initial attack power
	 */
	public AttackDesire(Creature target, int desirePower)
	{
		super(desirePower);
		this.target = target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleDesire(AI ai)
	{
		ai.setAiState(AIState.ATTACKING);
		
		Creature creature = ai.getOwner();

		if(creature.getTarget() == null)
		{
			creature.setTarget(target);
		}
		
		//TODO calculate delay (attack speed)
		task = new AttackTask(creature, target, 3000);
		task.handleTask();
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

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.desires.Desire#stopDesire()
	 */
	@Override
	public void stopDesire()
	{
		if(task != null)
		task.setTaskValid(false);
	}
}
