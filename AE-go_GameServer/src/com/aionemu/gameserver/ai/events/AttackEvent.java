/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.ai.events;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.task.AttackTask;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author ATracer
 *
 */
public class AttackEvent implements AIEvent
{

	private Creature originator;

	public AttackEvent(Creature originator)
	{
		super();
		this.originator = originator;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.events.AIEvent#handleEvent(com.aionemu.gameserver.ai.AI)
	 */
	@Override
	public void handleEvent(AI ai)
	{
		if(ai.isBusyForTask(AttackTask.PRIORITY))
		{
			return;
		}
		
		ai.setAiState(AIState.ATTACKING);
		
		Creature creature = ai.getOwner();

		if(creature.getTarget() == null)
		{
			creature.setTarget(originator);
		}
		
		//TODO calculate delay
		ai.startNewTask(new AttackTask(creature, originator, 3000));
	}

	/**
	 * @return the originator
	 */
	public Creature getOriginator()
	{
		return originator;
	}

	/**
	 * @param originator the originator to set
	 */
	public void setOriginator(Creature originator)
	{
		this.originator = originator;
	}
	
	
	
}
