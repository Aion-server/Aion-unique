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

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This class indicates that character wants to attack somebody
 * 
 * @author SoulKeeper
 */
public final class AttackDesire extends AbstractDesire
{

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
	protected AttackDesire(Creature target, int desirePower)
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
		// TODO: Implement
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
