/*  
 *  This file is part of aion-unique <aion-unique.org>.
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
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author Pinguin, ATracer
 *
 */
public class MoveToTargetDesire extends AbstractDesire implements MoveDesire
{
	private Npc owner;
	private Creature target;	
	
	/**
	 * @param crt 
	 * @param desirePower
	 */
	public MoveToTargetDesire(Npc owner, Creature target, int desirePower)
	{
		super(desirePower);
		this.owner = owner;
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleDesire(AI ai)
	{
		if (owner == null || owner.getLifeStats().isAlreadyDead())
			return false;
		if(target == null || target.getLifeStats().isAlreadyDead())
			return false;
		
		owner.getMoveController().setFollowTarget(true);
		
		if(!owner.getMoveController().isScheduled())
			owner.getMoveController().schedule();

		double distance = owner.getMoveController().getDistanceToTarget();
		if(distance > 150)
			return false;
		
		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof MoveToTargetDesire))
			return false;

		MoveToTargetDesire that = (MoveToTargetDesire) o;
		return target.equals(that.target);
	}

	/**
	 * @return the target
	 */
	public Creature getTarget()
	{	
		return target;
	}

	@Override
	public int getExecutionInterval()
	{
		return 1;
	}

	@Override
	public void onClear()
	{
		owner.getMoveController().stop();
	}
}