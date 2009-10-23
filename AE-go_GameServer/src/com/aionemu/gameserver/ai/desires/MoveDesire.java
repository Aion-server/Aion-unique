/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.ai.desires;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.task.AiTask;
import com.aionemu.gameserver.ai.task.MoveTask;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author Pinguin
 *
 */
public class MoveDesire extends AbstractDesire
{
	AiTask task;
	
	private Creature	target;

	/**
	 * @param desirePower
	 */
	public MoveDesire(Creature target, int desirePower)
	{
		super(desirePower);
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.desires.Desire#handleDesire(com.aionemu.gameserver.ai.AI)
	 */
	@Override
	public void handleDesire(AI ai)
	{
		ai.setAiState(AIState.MOVING);
		
		Creature creature = ai.getOwner();
		
		//TODO calculate delay (moving speed)
		task = new MoveTask((Npc) creature, target, 250);
		task.run();
		
	}
	
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof MoveDesire))
			return false;

		MoveDesire that = (MoveDesire) o;

		return target.equals(that.target);
	}
	
	public void stopDesire()
	{
		if(task != null)
		task.setTaskValid(false);
	}

}
